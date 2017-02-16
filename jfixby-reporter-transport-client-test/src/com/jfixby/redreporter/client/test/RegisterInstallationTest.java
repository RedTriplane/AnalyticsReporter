
package com.jfixby.redreporter.client.test;

import java.io.IOException;

import com.jfixby.redreporter.api.transport.ReporterTransport;
import com.jfixby.redreporter.client.http.ReporterHttpClient;
import com.jfixby.redreporter.client.http.ReporterHttpClientConfig;
import com.jfixby.scarabei.adopted.gdx.json.GoogleGson;
import com.jfixby.scarabei.api.desktop.ScarabeiDesktop;
import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.file.LocalFileSystem;
import com.jfixby.scarabei.api.json.Json;
import com.jfixby.scarabei.api.net.http.Http;
import com.jfixby.scarabei.api.net.http.HttpURL;
import com.jfixby.scarabei.api.sys.Sys;

public class RegisterInstallationTest {

	public static void main (final String[] args) throws IOException {
		ScarabeiDesktop.deploy();
		Json.installComponent(new GoogleGson());
// final File cache = LocalFileSystem.ApplicationHome().child("report-cache");
		final ReporterHttpClientConfig config = new ReporterHttpClientConfig();

		{
			final String url_string = "http://localhost:8080/api";
			final HttpURL url = Http.newURL(url_string);
// config.addAnalyticsServerUrl(url);
		}

		{
			final String url_string = "https://rr-0.red-triplane.com";
			final HttpURL url = Http.newURL(url_string);
			config.addAnalyticsServerUrl(url);
		}
		{
			final String url_string = "https://rr-1.red-triplane.com";
			final HttpURL url = Http.newURL(url_string);
			config.addAnalyticsServerUrl(url);
		}

		final File iidStorage = LocalFileSystem.ApplicationHome();
		config.setInstallationIDStorageFolder(iidStorage);
		final ReporterTransport client = new ReporterHttpClient(config);
		Sys.exit();

	}

}
