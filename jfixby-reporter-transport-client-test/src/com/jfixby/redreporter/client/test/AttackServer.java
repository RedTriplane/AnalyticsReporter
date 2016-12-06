
package com.jfixby.redreporter.client.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;

import com.jfixby.cmns.adopted.gdx.json.RedJson;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.file.LocalFileSystem;
import com.jfixby.cmns.api.json.Json;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.net.http.Http;
import com.jfixby.cmns.api.net.http.HttpURL;
import com.jfixby.cmns.api.sys.Sys;
import com.jfixby.red.desktop.DesktopSetup;
import com.jfixby.redreporter.api.transport.ServersCheck;
import com.jfixby.redreporter.api.transport.ServersCheckParams;
import com.jfixby.redreporter.client.http.ReporterHttpClient;
import com.jfixby.redreporter.client.http.ReporterHttpClientConfig;

public class AttackServer {

	public static void main (final String[] args) throws MalformedURLException, IOException {
		DesktopSetup.deploy();
		Json.installComponent(new RedJson());

		final ReporterHttpClientConfig transport_config = new ReporterHttpClientConfig();
		{
			final String url_string = "https://rr-0.red-triplane.com/";
			final HttpURL url = Http.newURL(url_string);
// transport_config.addAnalyticsServerUrl(url);
		}
		{
			final String url_string = "https://rr-1.red-triplane.com/";
			final HttpURL url = Http.newURL(url_string);
			transport_config.addAnalyticsServerUrl(url);
		}
		{
			final String url_string = "http://127.0.0.1:8080/";
			final HttpURL url = Http.newURL(url_string);
// transport_config.addAnalyticsServerUrl(url);
		}

		{
			final String url_string = "http://ec2-35-156-168-248.eu-central-1.compute.amazonaws.com/";
			final HttpURL url = Http.newURL(url_string);
// transport_config.addAnalyticsServerUrl(url);
		}

		final File iidStorage = LocalFileSystem.ApplicationHome();
		transport_config.setInstallationIDStorageFolder(iidStorage);
		transport_config.setIIDFileName("ping.test.id");
		final File logs = LocalFileSystem.ApplicationHome().child("logs");
		logs.makeFolder();
		transport_config.setCacheFolder(logs);
		final ReporterHttpClient transport = new ReporterHttpClient(transport_config);

		final int PARALLEL_CONNECTIONS = 50;

// final ServersCheckParams params = transport.newServersCheckParams();
// params.setTimeOut(1500L);
// final ServersCheck check = transport.checkServers(params);
// final long clockStart = Sys.SystemTime().currentTimeMillis();
// long clock = 0;
// clock = Sys.SystemTime().currentTimeMillis() - clockStart;
// check.print("" + clock);

// Sys.exit();
		for (int i = 0; i < PARALLEL_CONNECTIONS; i++) {
			final Thread t = new Thread() {
				@Override
				public void run () {
					while (true) {
						final ServersCheckParams params = transport.newServersCheckParams();
						params.setTimeOut(30000L);
						final ServersCheck check = transport.checkServers(params);
						final long id = requests_made++;
						L.d("requests_made", requests_made + " " + new Date());
						while (!check.isComplete()) {
							Sys.sleep(1500);
						}
						check.print(id + "");
					}
				}
			};
			t.start();
		}

	}

	static long requests_made = 0;

}
