
package com.jfixby.redreporter.red;

import java.io.IOException;

import com.jfixby.cmns.api.collections.Collection;
import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.List;
import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.err.Err;
import com.jfixby.cmns.api.err.ErrorComponent;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.log.LoggerComponent;
import com.jfixby.cmns.api.sys.Sys;
import com.jfixby.cmns.api.taskman.Job;
import com.jfixby.redreporter.api.InstallationID;
import com.jfixby.redreporter.api.PRIORITY;
import com.jfixby.redreporter.api.analytics.Report;
import com.jfixby.redreporter.api.err.ErrorReporterComponent;

public abstract class RedReporter implements ErrorReporterComponent {
	public static final boolean CACHE_FOLDER_SUCCESSFULLY_CREATED = true;
	public static final boolean CACHE_FOLDER_IS_TEMPORARY = false;
	final RedReporterUncaughtExceptionHandler uncaughtExceptionHandler = new RedReporterUncaughtExceptionHandler(this);
	final RedReporterErrorsListener errorsListener = new RedReporterErrorsListener(this);
	final RedReporterLoggerListener logsListener = new RedReporterLoggerListener(this);
	final ReportsWriter writer = new ReportsWriter(this);
	final ReportsQueue queue = new ReportsQueue(this);

	@Override
	public void reportProblem (final Throwable e) {
		final Report report = this.writer.newReport();
		report.setPriority(PRIORITY.NORMAL);
		report.addError(e);
		report.submit();
		Err.reportNotImplementedYet();
	}

	@Override
	public void reportCrash (final Throwable e) {
		final Report report = this.writer.newReport();
		report.setPriority(PRIORITY.URGENT_SEND_NOW);
		report.addError(e);
		report.submit();
		Err.reportNotImplementedYet();
	}

	public RedReporter (final File appHomeFolder) {

		this.home = appHomeFolder;
		Debug.checkNull("home", this.home);
		try {
			this.home.makeFolder();
			Debug.checkTrue("home is folder", this.home.isFolder());
		} catch (final IOException e) {
			e.printStackTrace();
			Err.reportError(e);
		}

	}

	public File getHomeFolder () {
		return this.home;
	}

	private final File home;

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

	public Report newReport () {
		return this.writer.newReport();
	}

	public void submit (final RedReport redReport) {
		this.queue.submit(redReport);
	}

	private final TryToDeployCacheJob tryToDeployCacheJob = new TryToDeployCacheJob(this);
	private final GetInstallationIDJob getInstallationIDJob = new GetInstallationIDJob(this);
	private File logCache;
	boolean cacheFolderState;;

	public Collection<Job> buildJobList () {
		final List<Job> jobs = Collections.newList();
		jobs.add(this.tryToDeployCacheJob);
		jobs.add(this.getInstallationIDJob);
		jobs.add(this.queue.getProcessQueueJob());
		return jobs;
	}

	public void setupCacheFolder (final File logCache, final boolean cacheFolderState) {
		this.cacheFolderState = cacheFolderState;
		this.logCache = logCache;
	}

	public static final long m1 = 60 * 1000;
	public static final long m10 = m1 * 10;
	public static final long H1 = m1 * 60;
	public static final long H4 = 4 * H1;

	private long sleep_attempt = 0;
	private long base = 1000;
	private long delta = 1000;
	private InstallationID installationID;

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

	public void setupInstallationID (final InstallationID id) {
		this.installationID = id;
	}

}
