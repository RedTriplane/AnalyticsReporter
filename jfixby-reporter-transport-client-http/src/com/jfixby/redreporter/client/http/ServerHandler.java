
package com.jfixby.redreporter.client.http;

import java.io.IOException;

import com.jfixby.cmns.api.collections.Mapping;
import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.io.IO;
import com.jfixby.cmns.api.java.ByteArray;
import com.jfixby.cmns.api.net.http.Http;
import com.jfixby.cmns.api.net.http.HttpConnection;
import com.jfixby.cmns.api.net.http.HttpConnectionInputStream;
import com.jfixby.cmns.api.net.http.HttpConnectionOutputStream;
import com.jfixby.cmns.api.net.http.HttpConnectionSpecs;
import com.jfixby.cmns.api.net.http.HttpURL;
import com.jfixby.cmns.api.net.http.METHOD;
import com.jfixby.cmns.api.net.message.Message;
import com.jfixby.redreporter.api.ServerStatus;
import com.jfixby.redreporter.api.transport.REPORTER_PROTOCOL;

public class ServerHandler {

	private final HttpURL url;

	public ServerHandler (final HttpURL url) {
		this.url = url;
	}

	@Override
	public String toString () {
		return "Server[" + this.url + "]";
	}

	private ServerPing check () {
		final ServerHandler server = this;
		final ServerPing ping = new ServerPing();
		ping.code = -1;
		ping.server = server;
		ping.url = this.url;
		ping.ping = Long.MAX_VALUE;
		ping.status = ServerStatus.NO_RESPONSE;
		ping.serverVersion = ServerPing.UNKNOWN;
		ping.serverProcesingTime = Long.MAX_VALUE;
		updatePeek(ping);
		if (ping.code != 200) {
			return ping;
		}
		updatePing(ping);
		return ping;
	}

	static private void updatePing (final ServerPing ping) {

		ping.ping = Long.MAX_VALUE;
		final long timestamp = System.currentTimeMillis();
		final Message pingMessage = new Message(REPORTER_PROTOCOL.PING);
		final Message pongMessage = ping.server.exchange(pingMessage);
		try {
			ping.status = (ServerStatus)pongMessage.attachments.get(REPORTER_PROTOCOL.SERVER_STATUS);
			ping.serverProcesingTime = (Long)pongMessage.attachments.get(REPORTER_PROTOCOL.SERVER_RESPONDED_IN);
			ping.serverVersion = pongMessage.values.get(REPORTER_PROTOCOL.SERVER_CODE_VERSION);
			ping.ping = System.currentTimeMillis() - timestamp;
		} catch (final Throwable e) {
			ping.error = e;
		}

	}

	static private int updatePeek (final ServerPing ping) {
		ping.code = -1;
		try {
			final HttpConnectionSpecs spec = Http.newConnectionSpecs();
			spec.setURL(ping.url);
			spec.setDoInput(true);

			final HttpConnection connect = Http.newConnection(spec);
			connect.open();

			ping.code = connect.getResponseCode();
			connect.close();
		} catch (final IOException e) {
			ping.code = -1;
		}
		return ping.code;
	}

	public HttpURL getUrl () {
		return this.url;
	}

	public Message exchange (final Message message) {
		try {
			return this.exchange(message, null);
		} catch (final IOException e) {
		}
		return null;
	}

	private Message exchange (final Message message, final Mapping<String, String> headers) throws IOException {
		Debug.checkNull("message", message);
		final HttpConnectionSpecs conSpec = Http.newConnectionSpecs();
		conSpec.setURL(this.url);

		conSpec.setMethod(METHOD.POST);
		conSpec.setUseCaches(false);
		conSpec.setDefaultUseCaches(false);
		conSpec.setDoInput(true);
		conSpec.setDoOutput(true);
		conSpec.setOctetStream(true);
		if (headers != null) {
			conSpec.addRequesrProperties(headers);
		}

		conSpec.setConnectTimeout(SERVER_TIMEOUT);
		conSpec.setReadTimeout(SERVER_TIMEOUT);

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
		final ByteArray rdata = is.readAll();
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

	public static final int SERVER_TIMEOUT = 5000;

	public void rank (final ServerRanker ranker) {
		final ServerHandler server = this;
		final Thread t = new Thread() {
			@Override
			public void run () {
				final ServerPing ping = ServerHandler.this.check();
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
