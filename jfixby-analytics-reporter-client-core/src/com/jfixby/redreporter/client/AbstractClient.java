
package com.jfixby.redreporter.client;

import java.io.IOException;

import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.file.File;
import com.jfixby.redreporter.api.AnalyticsReporterComponent;

public class AbstractClient implements AnalyticsReporterComponent {

	private final File reportingCache;

	public AbstractClient (final ClientConfig config) throws IOException {

		final File reportingCache = config.getReportingCache();
		Debug.checkNull("reportingCache", reportingCache);
		Debug.checkTrue("reportingCache is file", !reportingCache.isFile());
		reportingCache.makeFolder();
		this.reportingCache = reportingCache;
	}

}
