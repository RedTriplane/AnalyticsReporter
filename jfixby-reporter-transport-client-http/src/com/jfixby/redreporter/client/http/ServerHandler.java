
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
import com.jfixby.redreporter.api.transport.REPORTER_PROTOCOL;

public class ServerHandler {

	private final HttpURL url;
	private long ping = Long.MAX_VALUE;
	private int code;
	String status = NO_RESPONSE;
	String serverVersion = UNKNOWN;

	private String serverProcesingTime;
	static final String NO_RESPONSE = "NO_RESPONSE";
	private static final String UNKNOWN = "UNKNOWN";

	@Override
	public String toString () {
		return "[" + this.code() + "] " + this.url + " ping=" + this.ping() + " ServerState=" + this.status() + " processingTime="
			+ this.serverProcesingTime() + " serverVersion=" + this.serverVersion();
	}

	private String serverVersion () {
		return "<" + this.serverVersion + ">";
	}

	private String serverProcesingTime () {
		return this.serverProcesingTime;
	}

	private String status () {
		return "<" + this.status + ">";
	}

	public long getPing () {
		return this.ping;
	}

	public ServerHandler (final HttpURL url) {
		this.url = url;
	}

	public void check () {
		this.code = -1;
		this.ping = Long.MAX_VALUE;
		this.status = NO_RESPONSE;
		this.serverVersion = UNKNOWN;
		this.serverProcesingTime = UNKNOWN;
		this.updatePeek();
		if (this.code != 200) {
			return;
		}
		this.updatePing();
	}

	private void updatePing () {
		this.ping = Long.MAX_VALUE;
		final long timestamp = System.currentTimeMillis();
		final Message ping = new Message(REPORTER_PROTOCOL.PING);
		final Message pong = this.exchange(ping);
		this.ping = System.currentTimeMillis() - timestamp;

	}

	public int updatePeek () {
		this.code = -1;
		try {
			final HttpConnectionSpecs spec = Http.newConnectionSpecs();
			spec.setURL(this.url);
			spec.setDoInput(true);

			final HttpConnection connect = Http.newConnection(spec);
			connect.open();

			this.code = connect.getResponseCode();
			connect.close();
		} catch (final IOException e) {
			this.code = -1;
		}
		return this.code;
	}

	private String code () {
		if (this.code == -1) {
			return "X";
		}
		return this.code + "";
	}

	private String ping () {
		if (this.ping < 60000L) {
			return this.ping + "";
		}
		return "<NOT REACHABLE>";
	}

	public HttpURL getUrl () {
		return this.url;
	}

	public Message exchange (final Message message) {
		try {
			return this.exchange(message, null);
		} catch (final IOException e) {
			this.status = NO_RESPONSE;
			this.serverProcesingTime = Long.MAX_VALUE + "";
		}
		return null;

	}

	private Message exchange (final Message message, final Mapping<String, String> headers) throws IOException {
		Debug.checkNull("message", message);
		this.status = NO_RESPONSE;
		this.serverProcesingTime = UNKNOWN;
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
		this.serverProcesingTime = response.values.get(REPORTER_PROTOCOL.SERVER_RESPONDED_IN);
		this.status = response.values.get(REPORTER_PROTOCOL.SERVER_STATUS);
		this.serverVersion = response.values.get(REPORTER_PROTOCOL.SERVER_CODE_VERSION);
		return response;

	}

	public static final int SERVER_TIMEOUT = 14000;

}
