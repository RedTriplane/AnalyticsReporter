
package com.jfixby.redreporter.desktop;

import com.jfixby.redreporter.red.RedReporter;

public class DesktopReporter extends RedReporter {
	final DesktopReporterService service = new DesktopReporterService(this);

	public DesktopReporter (final DesktopReporterConfig deskCfg) {
		super(deskCfg.getLogsCache());
	}

	@Override
	public void startService () {
		this.service.start();

	}

	@Override
	public void stopService () {
	}

	@Override
	public void unDeployUncaughtExceptionHandler () {
	}

	@Override
	public void unDeployErrorsListener () {
	}

	@Override
	public void unDeployLogsListener () {
	}

}
