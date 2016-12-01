
package com.jfixby.redreporter.crash.desktop;

import com.jfixby.cmns.api.file.File;
import com.jfixby.redreporter.api.transport.ReporterTransport;
import com.jfixby.redreporter.crash.RedCrashReporter;

public class DesktopCrashReporter extends RedCrashReporter {
	final DesktopReporterService service = new DesktopReporterService(this);
	private final File cache;
	private final ReporterTransport transport;

	public DesktopCrashReporter (final DesktopReporterConfig deskCfg) {
		super(deskCfg.getTransport(), deskCfg.getLogsCache());
		this.cache = deskCfg.getLogsCache();
		this.transport = deskCfg.getTransport();

	}

	@Override
	public void startService () {
		this.service.start();

	}

	@Override
	public void stopService () {
		this.service.stop();
	}

}
