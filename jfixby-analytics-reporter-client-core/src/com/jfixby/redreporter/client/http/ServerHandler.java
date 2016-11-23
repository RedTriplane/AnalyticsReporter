
package com.jfixby.redreporter.client.http;

import java.io.IOException;

import com.jfixby.cmns.api.collections.Mapping;
import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.io.IO;
import com.jfixby.cmns.api.java.ByteArray;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.net.http.Http;
import com.jfixby.cmns.api.net.http.HttpConnection;
import com.jfixby.cmns.api.net.http.HttpConnectionInputStream;
import com.jfixby.cmns.api.net.http.HttpConnectionOutputStream;
import com.jfixby.cmns.api.net.http.HttpConnectionSpecs;
import com.jfixby.cmns.api.net.http.HttpURL;
import com.jfixby.cmns.api.net.http.METHOD;
import com.jfixby.cmns.api.net.message.Message;

public class ServerHandler {

	private final HttpURL url;
	private long ping = Long.MAX_VALUE;

	public ServerHandler (final HttpURL url) {
		this.url = url;
	}

	public long updatePing () {
		final long timestamp = System.currentTimeMillis();
		try {
			final HttpConnectionSpecs spec = Http.newConnectionSpecs();
			spec.setURL(this.url);
			spec.setDoInput(true);

			final HttpConnection connect = Http.newConnection(spec);
			connect.open();

			final int code = connect.getResponseCode();
			connect.close();
			this.ping = System.currentTimeMillis() - timestamp;
		} catch (final IOException e) {
			e.printStackTrace();
			this.ping = Long.MAX_VALUE;
		}
		return this.ping;
	}

	@Override
	public String toString () {
		return "" + this.url + " ping: " + this.ping() + "";
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

	public long getPing () {
		return this.ping;
	}

	public Message exchange (final Message message, final Mapping<String, String> p) {
		Debug.checkNull("message", message);
		try {

			final HttpConnectionSpecs conSpec = Http.newConnectionSpecs();
			conSpec.setURL(this.url);
			L.d("connecting", this.url);

			conSpec.setMethod(METHOD.POST);
			conSpec.setUseCaches(false);
			conSpec.setDefaultUseCaches(false);
			conSpec.setDoInput(true);
			conSpec.setDoOutput(true);
			conSpec.setOctetStream(true);

// final HttpURLConnection connection;
// final String urlStirng = this.url.getURLString();
// final URL url = new URL(urlStirng);
// connection = (HttpURLConnection)url.openConnection();
// connection.setRequestMethod("POST");

// connection.setRequestProperty("Content-Type", "application/octet-stream");

			conSpec.addRequesrProperties(p);

// for (final String key : p.keys()) {
// connection.setRequestProperty(key, p.get(key));
// }
			conSpec.setConnectTimeout(SERVER_TIMEOUT);
			conSpec.setReadTimeout(SERVER_TIMEOUT);
// connection.setConnectTimeout(SERVER_TIMEOUT); // TODO set to 10
// secs
// connection.setReadTimeout(SERVER_TIMEOUT);

			final HttpConnection connection = Http.newConnection(conSpec);

// connection.connect();
			connection.open();

// final java.io.OutputStream os = connection.getOutputStream();
//

			final HttpConnectionOutputStream os = connection.getOutputStream();
			os.open();
			final ByteArray data = IO.serialize(message);
			final ByteArray compressed = IO.compress(data);
			os.write(compressed.toArray());
			os.flush();
			os.close();

// final InputStream is = IO.newInputStream(new InputStreamOpener() {
// @Override
// public java.io.InputStream open () throws IOException {
// return connection.getInputStream();
// }
// });

			final HttpConnectionInputStream is = connection.getInputStream();
			is.open();
			final ByteArray rdata = is.readAll();
			is.close();
			final ByteArray responceBytes = IO.decompress(rdata);
			final Message response = IO.deserialize(Message.class, responceBytes);
			L.d("response", response);

			return response;
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public static final int SERVER_TIMEOUT = 14000;

}
