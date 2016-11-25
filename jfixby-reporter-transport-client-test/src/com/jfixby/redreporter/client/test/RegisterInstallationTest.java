
package com.jfixby.redreporter.client.test;

import java.io.IOException;

import com.jfixby.cmns.adopted.gdx.json.RedJson;
import com.jfixby.cmns.api.json.Json;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.net.http.Http;
import com.jfixby.cmns.api.net.http.HttpURL;
import com.jfixby.cmns.api.sys.Sys;
import com.jfixby.cmns.api.sys.SystemInfo;
import com.jfixby.red.desktop.DesktopSetup;
import com.jfixby.redreporter.api.InstallationID;
import com.jfixby.redreporter.api.transport.ReporterTransport;
import com.jfixby.redreporter.client.http.ReporterHttpClient;
import com.jfixby.redreporter.client.http.ReporterHttpClientConfig;

public class RegisterInstallationTest {

	public static void main (final String[] args) throws IOException {
		DesktopSetup.deploy();
		Json.installComponent(new RedJson());

// final File cache = LocalFileSystem.ApplicationHome().child("report-cache");
		final ReporterHttpClientConfig config = new ReporterHttpClientConfig();

		{
			final String url_string = "http://localhost:8080";
			final HttpURL url = Http.newURL(url_string);
			config.addAnalyticsServerUrl(url);
		}

		{
			final String url_string = "https://rr.red-triplane.com/";
			final HttpURL url = Http.newURL(url_string);
		}

		final SystemInfo systemInfo = Sys.getSystemInfo();

		final ReporterHttpClient client = new ReporterHttpClient(config);
		ReporterTransport.installComponent(client);
		final InstallationID installReg = ReporterTransport.registerInstallation(systemInfo);

		L.d("register install", installReg.token + " (" + installReg.token.length() + ")");
	}

}
