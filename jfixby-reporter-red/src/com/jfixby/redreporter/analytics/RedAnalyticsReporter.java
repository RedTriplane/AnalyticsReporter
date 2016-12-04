
package com.jfixby.redreporter.analytics;

import com.jfixby.cmns.api.assets.ID;
import com.jfixby.cmns.api.assets.Names;
import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.redreporter.api.analytics.AnalyticsReporterComponent;
import com.jfixby.redreporter.api.transport.ReporterTransport;

public class RedAnalyticsReporter implements AnalyticsReporterComponent {
	private final ID serviceID = Names.newID("com.red-triplane.reporter.analytics");
	private final ReporterTransport transport;

	public RedAnalyticsReporter (final ReporterTransport transport) {
		this.transport = Debug.checkNull("transport", transport);
	}

	public ReporterTransport getTransport () {
		return this.transport;
	}

	@Override
	public String toString () {
		return "RedAnalyticsReporter[" + this.serviceID + "]";
	}

}
