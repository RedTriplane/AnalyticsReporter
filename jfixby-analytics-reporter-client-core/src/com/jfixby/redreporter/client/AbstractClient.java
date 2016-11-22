
package com.jfixby.redreporter.client;

import java.io.IOException;

import com.jfixby.cmns.api.collections.CollectionScanner;
import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.Set;
import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.err.Err;
import com.jfixby.cmns.api.err.ErrorComponent;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.log.LoggerComponent;
import com.jfixby.cmns.api.net.http.Http;
import com.jfixby.cmns.api.net.http.HttpConnection;
import com.jfixby.cmns.api.net.http.HttpURL;
import com.jfixby.redreporter.api.AnalyticsReporterComponent;
import com.jfixby.redreporter.api.AnalyticsReporterErrorComponent;
import com.jfixby.redreporter.api.AnalyticsReporterLoggerComponent;

public class AbstractClient implements AnalyticsReporterComponent {

	private final File reportingCache;
	private final Set<HttpURL> servers = Collections.newSet();
	private final RedAnalyticsReporterLoggerComponent logger = new RedAnalyticsReporterLoggerComponent(this);
	private final RedAnalyticsReporterErrorComponent err = new RedAnalyticsReporterErrorComponent(this);

	public AbstractClient (final ClientConfig config) throws IOException {
		Debug.checkNull("config", config);

		final File reportingCache = config.getReportingCache();
		Debug.checkNull("reportingCache", reportingCache);
		Debug.checkTrue("reportingCache is file", !reportingCache.isFile());

		this.servers.addAll(config.listServers());
		this.servers.print("servers");

		reportingCache.makeFolder();
		this.reportingCache = reportingCache;

		if (config.wrapCurrentLogger()) {
			this.wrapCurrentLogger();
		}

		if (config.wrapCurrentErr()) {
			this.wrapCurrentErr();
		}

	}

	public void wrapCurrentLogger () {
		final AnalyticsReporterLoggerComponent arLog = this.getLogger();
		final LoggerComponent log = L.deInstallCurrentComponent();
		arLog.wrap(log);
		L.installComponent(arLog);
	}

	public void wrapCurrentErr () {
		final AnalyticsReporterErrorComponent arErr = this.getErr();
		final ErrorComponent err = Err.deInstallCurrentComponent();
		arErr.wrap(err);
		Err.installComponent(arErr);
	}

	@Override
	public void pingServers () {
		Collections.scanCollection(this.servers, new CollectionScanner<HttpURL>() {
			@Override
			public void scanElement (final HttpURL url, final int b) {
				L.d("ping " + url, AbstractClient.this.pingServer(url));
			}
		});
	}

	public boolean pingServer (final HttpURL url) {
		try {
			final HttpConnection connect = Http.newConnection(url);
			connect.open();
			final int code = connect.getResponseCode();
			connect.close();

			return code == 200;
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public AnalyticsReporterLoggerComponent getLogger () {
		return this.logger;
	}

	@Override
	public AnalyticsReporterErrorComponent getErr () {
		return this.err;
	}

}
