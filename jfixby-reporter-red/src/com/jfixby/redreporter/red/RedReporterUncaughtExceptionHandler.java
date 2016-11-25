
package com.jfixby.redreporter.red;

import java.lang.Thread.UncaughtExceptionHandler;

import com.jfixby.cmns.api.err.Err;

public class RedReporterUncaughtExceptionHandler implements UncaughtExceptionHandler {

	private final RedReporter master;

	public RedReporterUncaughtExceptionHandler (final RedReporter redReporter) {
		this.master = redReporter;
	}

	@Override
	public void uncaughtException (final Thread t, final Throwable e) {
		Err.reportError(e);
	}

}
