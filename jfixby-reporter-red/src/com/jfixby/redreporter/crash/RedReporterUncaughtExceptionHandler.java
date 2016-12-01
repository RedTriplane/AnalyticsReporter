
package com.jfixby.redreporter.crash;

import java.lang.Thread.UncaughtExceptionHandler;

import com.jfixby.cmns.api.err.Err;

public class RedReporterUncaughtExceptionHandler implements UncaughtExceptionHandler {

	private final RedCrashReporter master;
	private UncaughtExceptionHandler child;

	public RedReporterUncaughtExceptionHandler (final RedCrashReporter redReporter) {
		this.master = redReporter;
	}

	@Override
	public void uncaughtException (final Thread t, final Throwable e) {
		Err.reportError(t, e);
	}

	public void setChildHandler (final UncaughtExceptionHandler oldHandler) {
		this.child = oldHandler;
	}

	public UncaughtExceptionHandler getOldHandler () {
		return this.child;
	}

}
