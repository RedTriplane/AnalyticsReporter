
package com.jfixby.redreporter.analytics;

import com.jfixby.redreporter.api.analytics.AnalyticsReporterComponent;
import com.jfixby.redreporter.api.analytics.AnalyticsReporterEvents;
import com.jfixby.redreporter.api.analytics.ReportWriter;
import com.jfixby.redreporter.api.transport.ReporterTransport;
import com.jfixby.scarabei.api.debug.Debug;
import com.jfixby.scarabei.api.sys.settings.SystemSettings;
import com.jfixby.scarabei.api.ver.Version;

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
		writer.addStringValue(Version.Tags.VersionName, SystemSettings.getStringParameter(Version.Tags.VersionName));
		writer.addStringValue(Version.Tags.PackageName, SystemSettings.getStringParameter(Version.Tags.PackageName));
		writer.addStringValue(Version.Tags.VersionCode, SystemSettings.getStringParameter(Version.Tags.VersionCode));
		writer.submitReport();
	}

	@Override
	public ReportWriter newReportWriter () {
		return this.transport.newReportWriter();
	}

}
