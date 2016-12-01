
package com.jfixby.redreporter.crash.desktop;

import com.jfixby.redreporter.DesktopCrashReporterService;
import com.jfixby.redreporter.crash.RedCrashReporter;

public class DesktopCrashReporter extends RedCrashReporter {
	final DesktopCrashReporterService service = new DesktopCrashReporterService(this.getServiceJob());

	public DesktopCrashReporter (final DesktopReporterConfig deskCfg) {
		super(deskCfg.getTransport(), deskCfg.getLogsCache());
	}

	@Override
	public void startService () {
		this.service.start();
	}

	@Override
	public void stopService (final boolean wait) {
		this.requestServiceStop(wait);
	}
}
