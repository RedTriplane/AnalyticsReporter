
package com.jfixby.redreporter.api.client;

import com.jfixby.redreporter.api.AnalyticsReporterAPI;

public interface AnalyticsReporterComponent {

	void pingServers ();

	AnalyticsReporterLoggerComponent getLogger ();

	AnalyticsReporterErrorComponent getErr ();

	AnalyticsReporterAPI getAPI ();

}
