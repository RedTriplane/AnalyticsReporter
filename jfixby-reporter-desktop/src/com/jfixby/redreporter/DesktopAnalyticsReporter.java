
package com.jfixby.redreporter;

public class DesktopAnalyticsReporter extends RedAnalyticsReporter {
	final DesktopReporterService service = new DesktopReporterService(this.getServiceJob());

	public DesktopAnalyticsReporter (final DesktopAnalyticsReporterSpecs analytics_reporter_specs) {
		super(analytics_reporter_specs.getTransport(), analytics_reporter_specs.getLogsCache());
	}

	@Override
	public void startService () {
		this.service.start();

	}

	@Override
	public void stopService (final boolean wait) {
		this.requestServiceStop(wait);
	}

}
