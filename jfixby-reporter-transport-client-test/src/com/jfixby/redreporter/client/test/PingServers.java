
package com.jfixby.redreporter.client.test;

import java.io.IOException;

import com.jfixby.cmns.adopted.gdx.json.RedJson;
import com.jfixby.cmns.api.json.Json;
import com.jfixby.cmns.api.net.http.Http;
import com.jfixby.cmns.api.net.http.HttpURL;
import com.jfixby.red.desktop.DesktopSetup;
import com.jfixby.redreporter.api.DeviceInfo;
import com.jfixby.redreporter.api.Reporter;
import com.jfixby.redreporter.api.transport.DeviceRegistration;
import com.jfixby.redreporter.client.http.ReporterHttpClient;
import com.jfixby.redreporter.client.http.ReporterHttpClientConfig;
import com.jfixby.redreporter.desktop.DesktopReporter;
import com.jfixby.redreporter.desktop.DesktopReporterConfig;

public class PingServers {

	public static void main (final String[] args) throws IOException {
		DesktopSetup.deploy();
		Json.installComponent(new RedJson());

// final File cache = LocalFileSystem.ApplicationHome().child("report-cache");
		final ReporterHttpClientConfig config = new ReporterHttpClientConfig();

// config.setReportingCache(cache);

		{
			final String url_string = "http://localhost:8080";
			final HttpURL url = Http.newURL(url_string);

			config.addAnalyticsServerUrl(url);
		}
		{
			final String url_string = "https://ar.r3.jfixby.com/";
			final HttpURL url = Http.newURL(url_string);

// config.addAnalyticsServerUrl(url);
		}

		{
			final String url_string = "https://ar.red-triplane.com/";
			final HttpURL url = Http.newURL(url_string);

// config.addAnalyticsServerUrl(url);
		}
// config.setWrapCurrentLogger(true);
// config.setWrapCurrentErr(true);

		final DesktopReporterConfig deskCfg = new DesktopReporterConfig();
		Reporter.installComponent(new DesktopReporter(deskCfg));
//
// AnalyticsReporter.pingServers();
//
// final AnalyticsReporterAPI api = AnalyticsReporter.getAPI();
//
// final DeviceRegistration reg = api.registerDevice();

		final ReporterHttpClient client = new ReporterHttpClient(config);
		client.updatePings();
		client.printPings();

		final DeviceInfo deviceinfo = Reporter.getDeviceInfo();

		final DeviceRegistration deviceRegistration = client.registerDevice(deviceinfo);
		deviceRegistration.print();

	}

}
