
package com.jfixby.redreporter.api.analytics;

public interface AnalyticsReporterComponent {

	void startService ();

	void stopService ();

	void setServiceMode (SERVICE_MODE mode);

	SERVICE_MODE getServiceMode ();

}
