
package com.jfixby.redreporter.desktop;

import com.jfixby.cmns.api.file.File;

public class DesktopReporterConfig {

	private File logsCache;

	public void setLogsCache (final File logsCache) {
		this.logsCache = logsCache;
	}

	public File getLogsCache () {
		return this.logsCache;
	}

}
