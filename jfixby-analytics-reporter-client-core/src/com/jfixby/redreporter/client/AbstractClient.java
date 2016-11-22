
package com.jfixby.redreporter.client;

import java.io.IOException;

import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.net.http.Http;
import com.jfixby.cmns.api.net.http.HttpConnection;
import com.jfixby.cmns.api.net.http.HttpURL;
import com.jfixby.redreporter.api.AnalyticsReporterComponent;

public class AbstractClient implements AnalyticsReporterComponent {

	private final File reportingCache;
	private final HttpURL url;

	public AbstractClient (final ClientConfig config) throws IOException {
		Debug.checkNull("config", config);

		final File reportingCache = config.getReportingCache();
		Debug.checkNull("reportingCache", reportingCache);
		Debug.checkTrue("reportingCache is file", !reportingCache.isFile());

		this.url = config.getAnalyticsServerUrl();
		Debug.checkNull("url", this.url);

		reportingCache.makeFolder();
		this.reportingCache = reportingCache;
	}

	@Override
	public boolean pingServer () {
		try {
			final HttpConnection connect = Http.newConnection(this.url);
			connect.open();
			final int code = connect.getResponseCode();
			connect.close();

			return code == 200;
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return false;
	}

}
