
package com.jfixby.redreporter.client.http;

import java.io.IOException;

import com.jfixby.cmns.api.io.GZipInputStream;
import com.jfixby.cmns.api.io.GZipOutputStream;
import com.jfixby.cmns.api.io.IO;
import com.jfixby.cmns.api.net.http.Http;
import com.jfixby.cmns.api.net.http.HttpConnection;
import com.jfixby.cmns.api.net.http.HttpConnectionInputStream;
import com.jfixby.cmns.api.net.http.HttpConnectionOutputStream;
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
			final HttpConnection connect = Http.newConnection(this.url);
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

	public Message exchange (final Message msg) {
		try {
			final HttpConnection connection = Http.newConnection(this.url);
			connection.open();
			final HttpConnectionOutputStream os = connection.getOutputStream();
			final GZipOutputStream gzipos = IO.newGZipStream(os);
			IO.serialize(msg, gzipos);

			final HttpConnectionInputStream is = connection.getInputStream();
			final GZipInputStream gzipis = IO.newGZipStream(is);
			final Message result = IO.deserialize(Message.class, gzipis);
			connection.close();
			return result;
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}

	}

}
