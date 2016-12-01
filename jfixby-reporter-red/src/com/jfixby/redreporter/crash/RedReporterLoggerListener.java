
package com.jfixby.redreporter.crash;

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
			this.defaultLogger = new SimpleLogger();
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

}
