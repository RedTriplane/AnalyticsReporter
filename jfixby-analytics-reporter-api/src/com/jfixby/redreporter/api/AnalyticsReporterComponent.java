
package com.jfixby.redreporter.api;

public interface AnalyticsReporterComponent {

	void pingServers ();

	AnalyticsReporterLoggerComponent getLogger ();

	AnalyticsReporterErrorComponent getErr ();

}
