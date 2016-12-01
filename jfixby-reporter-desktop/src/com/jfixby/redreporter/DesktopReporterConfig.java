
package com.jfixby.redreporter;

import com.jfixby.cmns.api.file.File;
import com.jfixby.redreporter.api.transport.ReporterTransport;

public class DesktopReporterConfig {

	private File logsCache;
	private ReporterTransport transport;

	public void setLogsCache (final File logsCache) {
		this.logsCache = logsCache;
	}

	public File getLogsCache () {
		return this.logsCache;
	}

	public void setTransport (final ReporterTransport transport) {
		this.transport = transport;
	}

	public ReporterTransport getTransport () {
		return this.transport;
	}

}
