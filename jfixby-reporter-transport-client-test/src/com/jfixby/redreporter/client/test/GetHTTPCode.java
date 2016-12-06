
package com.jfixby.redreporter.client.test;

import java.io.IOException;

import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.net.http.Http;
import com.jfixby.cmns.api.net.http.HttpConnection;
import com.jfixby.cmns.api.net.http.HttpURL;
import com.jfixby.red.desktop.DesktopSetup;

public class GetHTTPCode {

	public static void main (final String[] args) throws IOException {
		DesktopSetup.deploy();
// final HttpURL url = Http.newURL("http://localhost:8080/health");
		final HttpURL url = Http.newURL("http://localhost:8080/health");
		final HttpConnection connect = Http.newConnection(url);
		connect.open();
		L.d(url, connect.getResponseCode());
		connect.close();
	}

}
