
package com.jfixby.redreporter.crash.desktop;

import com.jfixby.redreporter.crash.RedCrashReporter;

public class DesktopCrashReporter extends RedCrashReporter {
	final DesktopReporterService service = new DesktopReporterService(this);

	public DesktopCrashReporter (final DesktopReporterConfig deskCfg) {
		super(deskCfg.getTransport(), deskCfg.getLogsCache());

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
