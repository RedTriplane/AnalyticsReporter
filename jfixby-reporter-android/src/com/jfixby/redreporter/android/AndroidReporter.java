
package com.jfixby.redreporter.android;

import com.jfixby.redreporter.red.RedReporter;

public class AndroidReporter extends RedReporter {

	public AndroidReporter (final AndroidReporterConfig config) {
		super(config.getAppHomeFolder());
	}

	@Override
	public void startService () {
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
