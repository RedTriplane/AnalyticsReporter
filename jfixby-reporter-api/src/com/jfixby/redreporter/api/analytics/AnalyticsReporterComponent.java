
package com.jfixby.redreporter.api.analytics;

public interface AnalyticsReporterComponent {

	void startService ();

	void stopService ();

	void setServiceMode (AnalyticsReporterServiceMode mode);

}
