
package com.jfixby.redreporter.client.test;

import java.io.IOException;
import java.net.MalformedURLException;

import com.jfixby.cmns.adopted.gdx.json.RedJson;
import com.jfixby.cmns.api.java.ByteArray;
import com.jfixby.cmns.api.json.Json;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.net.http.Http;
import com.jfixby.cmns.api.net.http.HttpConnection;
import com.jfixby.cmns.api.net.http.HttpConnectionInputStream;
import com.jfixby.cmns.api.net.http.HttpURL;
import com.jfixby.cmns.api.util.JUtils;
import com.jfixby.red.desktop.DesktopSetup;

public class PingServer {

	public static void main (final String[] args) throws MalformedURLException, IOException {
		DesktopSetup.deploy();
		Json.installComponent(new RedJson());

		final String url_string = "https://rr-0.red-triplane.com/api";
		final HttpURL url = Http.newURL(url_string);

		final HttpConnection connect = Http.newConnection(url);
		connect.open();
		final HttpConnectionInputStream is = connect.getInputStream();
		is.open();
		final ByteArray data = is.readAll();
		is.close();
		connect.close();

		final String msg = JUtils.newString(data);
		L.d(msg);
	}

}
