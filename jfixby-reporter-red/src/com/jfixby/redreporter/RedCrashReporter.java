
package com.jfixby.redreporter;

import java.lang.Thread.UncaughtExceptionHandler;

import com.jfixby.cmns.api.err.Err;
import com.jfixby.cmns.api.err.ErrorComponent;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.file.FileFilter;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.log.LoggerComponent;
import com.jfixby.redreporter.api.crash.CrashReporterComponent;
import com.jfixby.redreporter.api.transport.ReporterTransport;

public abstract class RedCrashReporter extends AbstractReporter implements CrashReporterComponent {

	protected static final String CRASH_FILE_NAME_SUFFIX = ".crash.log";

	public RedCrashReporter (final ReporterTransport transport, final File logsCache) {
		super(transport, logsCache);
	}

	final RedReporterUncaughtExceptionHandler uncaughtExceptionHandler = new RedReporterUncaughtExceptionHandler(this);
	final RedReporterErrorsListener errorsListener = new RedReporterErrorsListener(this);
	final RedReporterLoggerListener logsListener = new RedReporterLoggerListener(this);
	private final FileFilter crash_files_filter = new FileFilter() {

		@Override
		public boolean fits (final File element) {
			return element.getName().endsWith(CRASH_FILE_NAME_SUFFIX);
		}
	};

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

	@Override
	void loadReportsFromCache () {
		this.loadReportsFromCache(this.crash_files_filter);
	}

	@Override
	String getLogFileExtention () {
		return CRASH_FILE_NAME_SUFFIX;
	}

}
