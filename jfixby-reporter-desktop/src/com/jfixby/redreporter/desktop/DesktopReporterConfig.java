
package com.jfixby.redreporter.desktop;

import com.jfixby.cmns.api.file.File;

public class DesktopReporterConfig {

	private File logsCache;

	public void setAppHomeFolder (final File logsCache) {
		this.logsCache = logsCache;
	}

	public File getAppHomeFolder () {
		return this.logsCache;
	}

}
