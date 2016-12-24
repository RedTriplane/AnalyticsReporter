
package com.jfixby.redreporter.crash;

import com.jfixby.scarabei.api.err.Err;
import com.jfixby.scarabei.api.err.ErrorComponent;
import com.jfixby.scarabei.red.err.RedError;

public class RedReporterErrorsListener implements ErrorComponent {

	private final RedCrashReporter master;
	private ErrorComponent defaultErrorListener;
	private ErrorComponent child;

	public RedReporterErrorsListener (final RedCrashReporter redReporter) {
		this.master = redReporter;
	}

	public void setChildListener (final ErrorComponent defaultErrorListener) {
		this.defaultErrorListener = defaultErrorListener;
		this.child = defaultErrorListener;
	}

	public ErrorComponent getChildListener () {
		return this.child;
	}

	public void deploy () {
		if (this.defaultErrorListener == null) {
			this.defaultErrorListener = new RedError();
		}
	}

	@Override
	public void reportError (final String message) {
		this.defaultErrorListener.reportError(message);

	}

	@Override
	public void reportError (final Throwable e) {
		this.defaultErrorListener.reportError(e);

	}

	@Override
	public void reportError (final String message, final Throwable e) {
		this.defaultErrorListener.reportError(message, e);

	}

	@Override
	public void reportNotImplementedYet () {
		this.defaultErrorListener.reportNotImplementedYet();
	}

	@Override
	public void reportGCLeak (final String msg, final Object leakingObject) {
		this.defaultErrorListener.reportGCLeak(msg, leakingObject);
	}

	@Override
	public void reportError (final Thread t, final Throwable e) {
		this.defaultErrorListener.reportError(t, e);
	}

	boolean enabled = false;

	public void enable () {
		if (this.enabled) {
			return;
		}
		this.enabled = true;
		final ErrorComponent oldErr = Err.component();
		if (oldErr != null) {
			Err.deInstallCurrentComponent();
			this.setChildListener(oldErr);
		}
		this.deploy();
		Err.installComponent(this);
	}

	public void disable () {
		if (!this.enabled) {
			return;
		}
		this.enabled = !true;
		Err.deInstallCurrentComponent();
		Err.installComponent(this.getChildListener());
	}

	public boolean isEnabled () {
		return this.enabled;
	}

}
