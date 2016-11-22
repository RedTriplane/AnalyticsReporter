
package com.jfixby.redreporter.client.http;

import java.io.IOException;

import com.jfixby.cmns.api.collections.Mapping;
import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.io.IO;
import com.jfixby.cmns.api.java.ByteArray;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.net.http.Http;
import com.jfixby.cmns.api.net.http.HttpCall;
import com.jfixby.cmns.api.net.http.HttpCallExecutor;
import com.jfixby.cmns.api.net.http.HttpCallParams;
import com.jfixby.cmns.api.net.http.HttpCallProgress;
import com.jfixby.cmns.api.net.http.HttpConnection;
import com.jfixby.cmns.api.net.http.HttpURL;
import com.jfixby.cmns.api.net.http.METHOD;
import com.jfixby.cmns.api.net.message.Message;
import com.jfixby.redreporter.api.PROTOCOL;

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

	public Message exchange (final Message msg, final Mapping<String, String> httpParams) {
		Debug.checkNull("message", msg);
		try {

			final HttpCallExecutor exe = Http.newCallExecutor();
			final HttpCallParams params = Http.newCallParams();

			params.setURL(this.url);
			params.setUseAgent(true);
			params.setMethod(METHOD.POST);
			params.setUseSSL(!true);

			final HttpCall call = Http.newCall(params);

			final ByteArray srlsdMsg = IO.serialize(msg);
			final ByteArray gzipped = IO.compress(srlsdMsg);

			call.addRequestHeader(PROTOCOL.MESSAGE, gzipped);
			call.addRequestHeaders(httpParams);

			final HttpCallProgress progress = exe.execute(call);

			final String response = progress.readResultAsString("utf-8");
			L.d("response", response);

// final ByteArray resultObjgzipped = (ByteArray)progress.readObject();
// final ByteArray srlsdResultMsg = IO.decompress(resultObjgzipped);
// final Message result = IO.deserialize(Message.class, srlsdResultMsg);
			final Message result = new Message();
			return result;
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}

	}

}
