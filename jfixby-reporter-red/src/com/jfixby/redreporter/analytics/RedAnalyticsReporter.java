
package com.jfixby.redreporter.analytics;

import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.redreporter.api.analytics.AnalyticsReporterComponent;
import com.jfixby.redreporter.api.analytics.AnalyticsReporterEvents;
import com.jfixby.redreporter.api.transport.ReportWriter;
import com.jfixby.redreporter.api.transport.ReporterTransport;

public class RedAnalyticsReporter implements AnalyticsReporterComponent {
	private final String authorID = ("com.red-triplane.reporter.analytics");
	private final ReporterTransport transport;

	public RedAnalyticsReporter (final ReporterTransport transport) {
		this.transport = Debug.checkNull("transport", transport);
	}

	public ReporterTransport getTransport () {
		return this.transport;
	}

	@Override
	public String toString () {
		return "RedAnalyticsReporter[" + this.authorID + "]";
	}

	@Override
	public void reportStart () {
		final ReportWriter writer = this.transport.newReportWriter();
		writer.setAuthor(this.authorID);
		writer.setSubject(AnalyticsReporterEvents.SERVICE_START);
		writer.submitReport();
	}

}
