
package com.jfixby.redreporter.api.analytics;

public interface AnalyticsReporterComponent {

	void startService ();

	void stopService (final boolean wait);

	void setServiceMode (SERVICE_MODE mode);

	SERVICE_MODE getServiceMode ();

}
