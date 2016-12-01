
package com.jfixby.redreporter.crash.desktop;

import com.jfixby.redreporter.crash.RedCrashReporter;

public class DesktopCrashReporter extends RedCrashReporter {
	final DesktopReporterService service = new DesktopReporterService(this);

	public DesktopCrashReporter (final DesktopReporterConfig deskCfg) {
		super(deskCfg.getTransport(), deskCfg.getLogsCache());

	}

}
