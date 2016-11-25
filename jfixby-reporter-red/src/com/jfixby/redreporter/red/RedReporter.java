
package com.jfixby.redreporter.red;

import com.jfixby.cmns.api.collections.Collection;
import com.jfixby.cmns.api.err.Err;
import com.jfixby.cmns.api.err.ErrorComponent;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.log.LoggerComponent;
import com.jfixby.cmns.api.taskman.Job;
import com.jfixby.redreporter.api.Report;
import com.jfixby.redreporter.api.ReporterComponent;

public abstract class RedReporter implements ReporterComponent {
	final RedReporterUncaughtExceptionHandler uncaughtExceptionHandler = new RedReporterUncaughtExceptionHandler(this);
	final RedReporterErrorsListener errorsListener = new RedReporterErrorsListener(this);
	final RedReporterLoggerListener logsListener = new RedReporterLoggerListener(this);
	final ReportsWriter writer = new ReportsWriter(this);
	final ReportsQueue queue = new ReportsQueue(this);

	@Override
	public void deployUncaughtExceptionHandler () {
		Thread.setDefaultUncaughtExceptionHandler(this.uncaughtExceptionHandler);
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
	public Report newReport () {
		return this.writer.newReport();
	}

	public void submit (final RedReport redReport) {
		this.queue.submit(redReport);
	}

	public Collection<Job> getJob () {
		return this.queue.getJob();
	}

}
