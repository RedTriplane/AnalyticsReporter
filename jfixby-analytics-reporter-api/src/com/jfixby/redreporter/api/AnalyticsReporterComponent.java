
package com.jfixby.redreporter.api;

public interface AnalyticsReporterComponent {

	boolean pingServer ();

	AnalyticsReporterLoggerComponent getLogger ();

	AnalyticsReporterErrorComponent getErr ();

}
