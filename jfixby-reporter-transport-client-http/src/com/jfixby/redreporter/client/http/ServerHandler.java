
package com.jfixby.redreporter.client.http;

import java.io.IOException;

import com.jfixby.redreporter.api.ServerStatus;
import com.jfixby.redreporter.api.transport.REPORTER_PROTOCOL;
import com.jfixby.scarabei.api.collections.Mapping;
import com.jfixby.scarabei.api.debug.Debug;
import com.jfixby.scarabei.api.io.IO;
import com.jfixby.scarabei.api.java.ByteArray;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.net.http.Http;
import com.jfixby.scarabei.api.net.http.HttpConnection;
import com.jfixby.scarabei.api.net.http.HttpConnectionInputStream;
import com.jfixby.scarabei.api.net.http.HttpConnectionOutputStream;
import com.jfixby.scarabei.api.net.http.HttpConnectionSpecs;
import com.jfixby.scarabei.api.net.http.HttpURL;
import com.jfixby.scarabei.api.net.http.METHOD;
import com.jfixby.scarabei.api.net.message.Message;

public class ServerHandler {

	private final HttpURL url;

	public ServerHandler (final HttpURL url) {
		this.url = url;
	}

	@Override
	public String toString () {
		return "Server[" + this.url + "]";
	}

	private ServerPing check (final RequestArgs args) {
		final ServerPing ping = new ServerPing();
		ping.code = -1;
		ping.server = this;
		ping.url = this.url;
		ping.ping = Long.MAX_VALUE;
		ping.status = ServerStatus.NO_RESPONSE;
		ping.serverVersion = ServerPing.UNKNOWN;
		ping.serverProcesingTime = Long.MAX_VALUE;
		try {
			updatePeek(ping, args);
			if (ping.code != 200) {
				return ping;
			}
			updatePing(ping, args);
		} catch (final Throwable e) {
			ping.error = L.stackTraceToString(e);
		}
		return ping;
	}

	static private void updatePing (final ServerPing ping, final RequestArgs args) {

		ping.ping = Long.MAX_VALUE;
		final long timestamp = System.currentTimeMillis();
		final Message pingMessage = new Message(REPORTER_PROTOCOL.PING);
		final Message pongMessage = ping.server.exchange(pingMessage, args);

		try {
			ping.request_id = pongMessage.values.get(REPORTER_PROTOCOL.REQUEST_ID);
		} catch (final Throwable e) {
			ping.error = "failed to read: serverVersion";
			return;
		}

		try {
			ping.status = (ServerStatus)pongMessage.attachments.get(REPORTER_PROTOCOL.SERVER_STATUS);
		} catch (final Throwable e) {
			ping.error = "failed to read: ServerStatus";
			return;
		}

		try {
			ping.serverProcesingTime = (Long)pongMessage.attachments.get(REPORTER_PROTOCOL.SERVER_RESPONDED_IN);
		} catch (final Throwable e) {
			ping.error = "failed to read: serverProcesingTime";
			return;
		}

		try {
			ping.serverVersion = pongMessage.values.get(REPORTER_PROTOCOL.SERVER_CODE_VERSION);
		} catch (final Throwable e) {
			ping.error = "failed to read: serverVersion";
			return;
		}

		ping.ping = System.currentTimeMillis() - timestamp;

	}

	static private int updatePeek (final ServerPing ping, final RequestArgs args) {
		ping.code = -1;
		try {
			final HttpConnectionSpecs spec = Http.newConnectionSpecs();
			spec.setURL(ping.url);
			spec.setDoInput(true);
			spec.setConnectTimeout(args.timeout);
			spec.setReadTimeout(args.timeout);
			if (args == null || args.timeout <= 0) {
				spec.setConnectTimeout(SERVER_DEFAULT_TIMEOUT);
				spec.setReadTimeout(SERVER_DEFAULT_TIMEOUT);
			} else {
				spec.setConnectTimeout(args.timeout);
				spec.setReadTimeout(args.timeout);
			}

			final HttpConnection connect = Http.newConnection(spec);
			connect.open();

			ping.code = connect.getResponseCode();
			connect.close();
		} catch (final IOException e) {
			ping.code = -1;
			ping.error = L.stackTraceToString(e);
		}
		return ping.code;
	}

	public HttpURL getUrl () {
		return this.url;
	}

	private Message exchange (final Message message, final RequestArgs args) {
		try {
			return exchange(message, null, this.url, args);
		} catch (final IOException e) {
		}
		return null;
	}

	Message exchange (final Message message) {
		return this.exchange(message, null);
	}

	static private Message exchange (final Message message, final Mapping<String, String> headers, final HttpURL url,
		final RequestArgs args) throws IOException {
		Debug.checkNull("message", message);
		final HttpConnectionSpecs conSpec = Http.newConnectionSpecs();
		conSpec.setURL(url);

		conSpec.setMethod(METHOD.POST);
		conSpec.setUseCaches(false);
		conSpec.setDefaultUseCaches(false);
		conSpec.setDoInput(true);
		conSpec.setDoOutput(true);
		conSpec.setOctetStream(true);
		if (headers != null) {
			conSpec.addRequesrProperties(headers);
		}

		if (args == null || args.timeout <= 0) {

			conSpec.setConnectTimeout(SERVER_DEFAULT_TIMEOUT);
			conSpec.setReadTimeout(SERVER_DEFAULT_TIMEOUT);
		} else {
			conSpec.setConnectTimeout(args.timeout);
			conSpec.setReadTimeout(args.timeout);
		}

		final HttpConnection connection = Http.newConnection(conSpec);

		connection.open();

		final HttpConnectionOutputStream os = connection.getOutputStream();
		os.open();
// message.print();

		final ByteArray data = IO.serialize(message);

		final ByteArray compressed = IO.compress(data);
		os.write(compressed.toArray());
		os.flush();
		os.close();

		final HttpConnectionInputStream is = connection.getInputStream();
		is.open();
		ByteArray rdata;
		try {
			rdata = is.readAll();
		} catch (final IOException e2) {
			e2.printStackTrace();
			rdata = null;
		}
		is.close();
		if (rdata == null) {
			return null;
		}
		if (rdata.size() == 0) {
			return null;
		}
		final ByteArray responceBytes = IO.decompress(rdata);
		if (responceBytes == null) {
			return null;
		}

		final Message response = IO.deserialize(Message.class, responceBytes);
		return response;

	}

	public static final int SERVER_DEFAULT_TIMEOUT = 3000;

	public void check (final ServerRanker ranker, final RequestArgs args) {
		final ServerHandler server = this;
		final Thread t = new Thread() {
			@Override
			public void run () {
				final ServerPing ping = ServerHandler.this.check(args);
				if (ping.isGood()) {
					ranker.onSuccess(server, ping);
				} else {
					ranker.onFail(server, ping);
				}
			}
		};
		t.start();
	}

}
