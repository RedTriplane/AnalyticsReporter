
package com.jfixby.redreporter.desktop;

import com.jfixby.redreporter.red.RedReporter;

public class DesktopReporter extends RedReporter {
	final DesktopReporterService service = new DesktopReporterService(this);

	public DesktopReporter (final DesktopReporterConfig deskCfg) {
		super(deskCfg.getAppHomeFolder());
	}

	@Override
	public void startService () {
		this.service.start();

	}

}
