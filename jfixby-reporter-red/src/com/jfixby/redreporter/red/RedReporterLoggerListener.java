
package com.jfixby.redreporter.red;

import com.jfixby.cmns.api.log.LoggerComponent;
import com.jfixby.cmns.api.log.MESSAGE_MARKER;
import com.jfixby.red.log.SimpleLogger;

public class RedReporterLoggerListener extends SimpleLogger implements LoggerComponent {

	private LoggerComponent defaultLogger;

	final LogsContainer container = new LogsContainer();

	private final RedReporter master;

	public RedReporterLoggerListener (final RedReporter redReporter) {
		this.master = redReporter;
	}

	public void setChildListener (final LoggerComponent defaultLogger) {
		this.defaultLogger = defaultLogger;
	}

	public void deploy () {
		if (this.defaultLogger == null) {
			this.defaultLogger = new SimpleLogger();
		}
	}

	@Override
	public void System_err_println (final Object string) {
		this.container.pushLine(MESSAGE_MARKER.ERR, string);
	}

	@Override
	public void System_out_println (final Object string) {
		this.container.pushLine(MESSAGE_MARKER.NORMAL, string);
	}

	@Override
	public void System_err_println () {
		this.container.pushLine(MESSAGE_MARKER.ERR, "");
	}

	@Override
	public void System_out_println () {
		this.container.pushLine(MESSAGE_MARKER.NORMAL, "");
	}

	@Override
	public void System_out_print (final Object string) {
		this.container.append(MESSAGE_MARKER.NORMAL, string);
	}

}
