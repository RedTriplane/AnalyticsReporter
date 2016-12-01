
package com.jfixby.redreporter.analytics.desktop;

import com.jfixby.redreporter.analytics.RedAnalyticsReporter;

public class DesktopAnalyticsReporter extends RedAnalyticsReporter {

	public DesktopAnalyticsReporter (final DesktopAnalyticsReporterSpecs analytics_reporter_specs) {
		super(analytics_reporter_specs.getTransport(), analytics_reporter_specs.getLogsCache());
	}

}
