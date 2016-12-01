
package com.jfixby.redreporter.analytics;

import com.jfixby.cmns.api.file.File;
import com.jfixby.redreporter.AbstractReporter;
import com.jfixby.redreporter.api.analytics.AnalyticsReporterComponent;
import com.jfixby.redreporter.api.transport.ReporterTransport;

public abstract class RedAnalyticsReporter extends AbstractReporter implements AnalyticsReporterComponent {

	public RedAnalyticsReporter (final ReporterTransport transport, final File logsCache) {
		super(transport, logsCache);
	}

}
