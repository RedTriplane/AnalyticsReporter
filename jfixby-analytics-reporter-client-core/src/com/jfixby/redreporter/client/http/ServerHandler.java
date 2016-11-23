
package com.jfixby.redreporter.client.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import com.jfixby.cmns.api.collections.Mapping;
import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.io.IO;
import com.jfixby.cmns.api.io.InputStream;
import com.jfixby.cmns.api.io.InputStreamOpener;
import com.jfixby.cmns.api.java.ByteArray;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.net.http.Http;
import com.jfixby.cmns.api.net.http.HttpConnection;
import com.jfixby.cmns.api.net.http.HttpConnectionSpecs;
import com.jfixby.cmns.api.net.http.HttpURL;
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
			final URLConnection urlConnection;
			L.d("connecting", this.url);
			final String urlStirng = this.url.getURLString();
			final URL url = new URL(urlStirng);
			urlConnection = url.openConnection();
			if (urlConnection instanceof HttpURLConnection) {
				((HttpURLConnection)urlConnection).setRequestMethod("POST");
			} else {
				new Error("this connection is NOT an HttpUrlConnection connection").printStackTrace();
				return null;
			}

			urlConnection.setUseCaches(false);
			urlConnection.setDefaultUseCaches(false);
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
			urlConnection.setRequestProperty("Content-Type", "application/octet-stream");

			for (final String key : p.keys()) {
				urlConnection.setRequestProperty(key, p.get(key));
			}

			urlConnection.setConnectTimeout(SERVER_TIMEOUT); // TODO set to 10
			// secs
			urlConnection.setReadTimeout(SERVER_TIMEOUT);

			urlConnection.connect();

			final java.io.OutputStream os = urlConnection.getOutputStream();

			final ByteArray data = IO.serialize(message);
			final ByteArray compressed = IO.compress(data);
			os.write(compressed.toArray());
			os.flush();
			os.close();

			final InputStream is = IO.newInputStream(new InputStreamOpener() {
				@Override
				public java.io.InputStream open () throws IOException {
					return urlConnection.getInputStream();
				}
			});
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
