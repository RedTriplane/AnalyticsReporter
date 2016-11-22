
package com.jfixby.redreporter.client.test;

import java.io.IOException;

import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.file.LocalFileSystem;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.net.http.Http;
import com.jfixby.cmns.api.net.http.HttpURL;
import com.jfixby.red.desktop.DesktopSetup;
import com.jfixby.redreporter.api.AnalyticsReporter;
import com.jfixby.redreporter.client.ClientConfig;
import com.jfixby.redreporter.client.desktop.DesktopReporter;

public class PingServer {

	public static void main (final String[] args) throws IOException {
		DesktopSetup.deploy();

		final File cache = LocalFileSystem.ApplicationHome().child("report-cache");
		final ClientConfig config = new ClientConfig();

		config.setReportingCache(cache);

		final String url_string = "https://ar.r3.jfixby.com/";
		final HttpURL url = Http.newURL(url_string);

		config.setAnalyticsServerUrl(url);

		config.setWrapCurrentLogger(true);
		config.setWrapCurrentErr(true);

		AnalyticsReporter.installComponent(new DesktopReporter(config));

		final boolean ping = AnalyticsReporter.pingServer();
		L.d("ping " + url, ping);

	}

}
