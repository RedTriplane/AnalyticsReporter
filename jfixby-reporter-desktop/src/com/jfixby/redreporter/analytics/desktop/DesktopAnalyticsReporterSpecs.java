
package com.jfixby.redreporter.analytics.desktop;

import com.jfixby.cmns.api.file.File;
import com.jfixby.redreporter.api.transport.ReporterTransport;

public class DesktopAnalyticsReporterSpecs {

	private File logs;
	ReporterTransport transport;

	public void setTransport (final ReporterTransport transport) {
		this.transport = transport;
	}

	public void setLogsCache (final File logs) {
		this.logs = logs;
	}

	public File getLogsCache () {
		return this.logs;
	}

	public ReporterTransport getTransport () {
		return this.transport;
	}

}
