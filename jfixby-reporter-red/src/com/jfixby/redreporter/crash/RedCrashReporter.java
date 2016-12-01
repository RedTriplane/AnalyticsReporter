
package com.jfixby.redreporter.crash;

import java.lang.Thread.UncaughtExceptionHandler;

import com.jfixby.cmns.api.err.Err;
import com.jfixby.cmns.api.err.ErrorComponent;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.log.LoggerComponent;
import com.jfixby.cmns.api.sys.Sys;
import com.jfixby.redreporter.AbstractReporter;
import com.jfixby.redreporter.api.crash.CrashReporterComponent;
import com.jfixby.redreporter.api.transport.ReporterTransport;

public abstract class RedCrashReporter extends AbstractReporter implements CrashReporterComponent {

	public RedCrashReporter (final ReporterTransport transport, final File logsCache) {
		super(transport, logsCache);
	}

	final RedReporterUncaughtExceptionHandler uncaughtExceptionHandler = new RedReporterUncaughtExceptionHandler(this);
	final RedReporterErrorsListener errorsListener = new RedReporterErrorsListener(this);
	final RedReporterLoggerListener logsListener = new RedReporterLoggerListener(this);

	@Override
	public void deployUncaughtExceptionHandler () {
		final UncaughtExceptionHandler oldHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this.uncaughtExceptionHandler);
		if (oldHandler != null) {
			this.uncaughtExceptionHandler.setChildHandler(oldHandler);
		}
	}

	@Override
	public void deployErrorsListener () {
		final ErrorComponent oldErr = Err.component();
		if (oldErr != null) {
			Err.deInstallCurrentComponent();
			this.errorsListener.setChildListener(oldErr);
		}
		this.errorsListener.deploy();
		Err.installComponent(this.errorsListener);
	}

	@Override
	public void deployLogsListener () {
		final LoggerComponent oldLogger = L.component();
		if (oldLogger != null) {
			L.deInstallCurrentComponent();
			this.logsListener.setChildListener(oldLogger);
		}
		this.logsListener.deploy();
		L.installComponent(this.logsListener);
	}

	@Override
	public void unDeployUncaughtExceptionHandler () {
		final UncaughtExceptionHandler oldHandler = this.uncaughtExceptionHandler.getOldHandler();
		Thread.setDefaultUncaughtExceptionHandler(oldHandler);
	}

	@Override
	public void unDeployErrorsListener () {
		Err.deInstallCurrentComponent();
		Err.installComponent(this.errorsListener.getChildListener());
	}

	@Override
	public void unDeployLogsListener () {
		L.deInstallCurrentComponent();
		L.installComponent(this.logsListener.getChild());
	}

	// ------------------------------------------------------------------------------------------------

	public static final long m1 = 60 * 1000;
	public static final long m10 = m1 * 10;
	public static final long H1 = m1 * 60;
	public static final long H4 = 4 * H1;

	private long sleep_attempt = 0;
	private long base = 1000;
	private long delta = 1000;

	public void resetSleep () {
		this.base = 1000;
		this.delta = 1000;
		this.sleep_attempt = 0;
	}

	public void speep (final long max) {
		this.sleep_attempt++;
		long sleepTime = this.base + this.sleep_attempt * this.sleep_attempt * this.delta;
		if (sleepTime > max) {
			sleepTime = max;
		}
		Sys.sleep(sleepTime);
	}

}
