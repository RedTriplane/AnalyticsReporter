
package com.jfixby.redreporter.crash;

import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.log.LoggerComponent;
import com.jfixby.cmns.api.log.MESSAGE_MARKER;
import com.jfixby.red.log.SimpleLogger;

public class RedReporterLoggerListener extends SimpleLogger implements LoggerComponent {

	private LoggerComponent defaultLogger;

	private final RedCrashReporter master;

	private LoggerComponent child;

	public RedReporterLoggerListener (final RedCrashReporter redReporter) {
		this.master = redReporter;
	}

	public void setChildListener (final LoggerComponent defaultLogger) {
		this.defaultLogger = defaultLogger;
		this.child = defaultLogger;
	}

	public LoggerComponent getChild () {
		return this.child;
	}

	public void deploy () {
		if (this.defaultLogger == null) {
			this.defaultLogger = this;
		}
	}

	@Override
	public void logLine (final MESSAGE_MARKER marker, final Object string) {
		super.logLine(marker, string);
	}

	@Override
	public void logAppend (final MESSAGE_MARKER marker, final Object string) {
		super.logAppend(marker, string);
	}

	@Override
	public void logLine (final MESSAGE_MARKER marker) {
		super.logLine(marker);
	}

	@Override
	public void logAppend (final MESSAGE_MARKER marker) {
		super.logAppend(marker);
	}

	boolean enabled = false;

	public void enable () {
		if (this.enabled) {
			return;
		}
		this.enabled = true;
		final LoggerComponent oldLogger = L.component();
		if (oldLogger != null) {
			L.deInstallCurrentComponent();
			this.setChildListener(oldLogger);
		}
		this.deploy();
		L.installComponent(this);
	}

	public void disable () {
		if (!this.enabled) {
			return;
		}
		this.enabled = !true;
		L.deInstallCurrentComponent();
		L.installComponent(this.getChild());
	}

	public boolean isEnabled () {
		return this.enabled;
	}

}
