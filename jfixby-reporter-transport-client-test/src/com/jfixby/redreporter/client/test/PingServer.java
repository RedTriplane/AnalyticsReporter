
package com.jfixby.redreporter.client.test;

import java.io.IOException;
import java.net.MalformedURLException;

import com.jfixby.cmns.adopted.gdx.json.RedJson;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.file.LocalFileSystem;
import com.jfixby.cmns.api.json.Json;
import com.jfixby.cmns.api.net.http.Http;
import com.jfixby.cmns.api.net.http.HttpURL;
import com.jfixby.red.desktop.DesktopSetup;
import com.jfixby.redreporter.api.transport.ReporterTransport;
import com.jfixby.redreporter.client.http.ReporterHttpClient;
import com.jfixby.redreporter.client.http.ReporterHttpClientConfig;

public class PingServer {

	public static void main (final String[] args) throws MalformedURLException, IOException {
		DesktopSetup.deploy();
		Json.installComponent(new RedJson());

		final ReporterHttpClientConfig transport_config = new ReporterHttpClientConfig();
		{
			final String url_string = "https://rr-0.red-triplane.com/";
			final HttpURL url = Http.newURL(url_string);
			transport_config.addAnalyticsServerUrl(url);
		}
// {
// final String url_string = "http://127.0.0.1:8080/";
// final HttpURL url = Http.newURL(url_string);
// transport_config.addAnalyticsServerUrl(url);
// }
		final File iidStorage = LocalFileSystem.ApplicationHome();
		transport_config.setInstallationIDStorageFolder(iidStorage);
		final ReporterHttpClient transport = new ReporterHttpClient(transport_config);
		ReporterTransport.installComponent(transport);
		while (true) {
			ReporterTransport.checkServers();
		}
	}
// final String url_string = "https://rr-0.red-triplane.com";
// final HttpURL url = Http.newURL(url_string);
//
// final HttpConnection connect = Http.newConnection(url);
// connect.open();
// final HttpConnectionInputStream is = connect.getInputStream();
// is.open();
// final ByteArray data = is.readAll();
// is.close();
// connect.close();
//
// final String msg = JUtils.newString(data);
// L.d(msg);
// }

}
