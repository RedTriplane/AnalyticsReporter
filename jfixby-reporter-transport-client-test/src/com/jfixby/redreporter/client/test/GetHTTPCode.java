
package com.jfixby.redreporter.client.test;

import java.io.IOException;

import com.jfixby.scarabei.api.desktop.ScarabeiDesktop;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.net.http.Http;
import com.jfixby.scarabei.api.net.http.HttpConnection;
import com.jfixby.scarabei.api.net.http.HttpURL;

public class GetHTTPCode {

	public static void main (final String[] args) throws IOException {
		ScarabeiDesktop.deploy();
// final HttpURL url = Http.newURL("http://localhost:8080/health");
		final HttpURL url = Http.newURL("http://localhost:8080/health");
		final HttpConnection connect = Http.newConnection(url);
		connect.open();
		L.d(url, connect.getResponseCode());
		connect.close();
	}

}
