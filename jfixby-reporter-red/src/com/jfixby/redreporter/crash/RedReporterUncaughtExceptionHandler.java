
package com.jfixby.redreporter.crash;

import java.lang.Thread.UncaughtExceptionHandler;

import com.jfixby.cmns.api.err.Err;

public class RedReporterUncaughtExceptionHandler implements UncaughtExceptionHandler {

	private UncaughtExceptionHandler child;

	public RedReporterUncaughtExceptionHandler () {
	}

	@Override
	public void uncaughtException (final Thread t, final Throwable e) {
		Err.reportError(t, e);
	}

	public void setChildHandler (final UncaughtExceptionHandler oldHandler) {
		this.child = oldHandler;
	}

	boolean enabled = false;

	public void enable () {
		if (this.enabled) {
			return;
		}
		final UncaughtExceptionHandler oldHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
		if (oldHandler != null) {
			this.setChildHandler(oldHandler);
		}
		this.enabled = true;
	}

	public void disable () {
		if (!this.enabled) {
			return;
		}
		Thread.setDefaultUncaughtExceptionHandler(this.child);
		this.enabled = !true;
	}

	public boolean isEnabled () {
		return this.enabled;
	}

}
