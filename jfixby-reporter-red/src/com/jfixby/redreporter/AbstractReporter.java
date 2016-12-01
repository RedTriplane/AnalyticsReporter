
package com.jfixby.redreporter;

import java.io.IOException;

import com.jfixby.cmns.api.collections.Collection;
import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.List;
import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.err.Err;
import com.jfixby.cmns.api.file.ChildrenList;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.file.FileFilter;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.sys.Sys;
import com.jfixby.cmns.api.taskman.Job;
import com.jfixby.redreporter.api.analytics.SERVICE_MODE;
import com.jfixby.redreporter.api.transport.ReporterTransport;

public abstract class AbstractReporter {
	private static final boolean OK = true;
	private final File logsCache;
	private final ReporterTransport transport;
	private boolean cacheIsValid;
	private SERVICE_MODE mode = SERVICE_MODE.NOISY;

	public AbstractReporter (final ReporterTransport transport, final File logsCache) {
		this.logsCache = Debug.checkNull("logsCache", logsCache);
		try {
			this.logsCache.makeFolder();
			this.cacheIsValid = true;
		} catch (final IOException e) {
			e.printStackTrace();
			Err.reportError(e);
			this.cacheIsValid = false;
		}
		this.transport = Debug.checkNull("transport", transport);
	}

	public File getCache () {
		if (!this.cacheIsValid) {
			return null;
		}
		return this.logsCache;
	}

	public ReporterTransport getTransport () {
		return this.transport;
	}

	final ReportsQueue queue = new ReportsQueue();

	private void start () {
		L.d("doStart", this);
		this.stopJob = false;
	}

	private final void push () {
// L.d("push", this);
		{
			// --------------------------------
			if (this.mode == SERVICE_MODE.QUIET) {
				Sys.sleep(this.period_long);
				return;
			}
			if (this.queue.size() == 0) {
				Sys.sleep(this.period);
				return;
			}
			final ReportHandler report = this.queue.peek();
			final boolean result = this.transport.sendReport(report.data);
			if (result == OK) {
				report.dispose();
				this.queue.remove();
				return;
			}

			this.queue.ensureCached(this.getCache(), this.getLogFileExtention());
			// --------------------------------
		}
// Sys.sleep(1000);
	}

	abstract String getLogFileExtention ();

	final private long period = 100;
	final private long period_long = 5000;

	abstract void loadReportsFromCache ();

	final Job loadCache = new Job() {

		@Override
		public void doStart () throws Throwable {
			AbstractReporter.this.loadReportsFromCache();
		}

		@Override
		public void doPush () throws Throwable {
		}

		@Override
		public boolean isDone () {
			return true;
		}
	};
	final Job pushService = new Job() {

		@Override
		public void doStart () throws Throwable {
			AbstractReporter.this.start();
		}

		@Override
		public void doPush () throws Throwable {
			AbstractReporter.this.push();
		}

		@Override
		public boolean isDone () {
			if (AbstractReporter.this.stopJob) {
				AbstractReporter.this.serviceIsStopping = false;
			}
			return AbstractReporter.this.stopJob;
		}
	};
	final List<Job> serviceJob = Collections.newList(this.loadCache, this.pushService);

	private boolean stopJob;
	boolean serviceIsStopping = false;

	public void requestServiceStop (final boolean wait) {
		this.serviceIsStopping = true;
		this.stopJob = true;
		while (wait && this.serviceIsStopping) {
			Sys.sleep(1);
		}
	}

	public Collection<Job> getServiceJob () {
		return this.serviceJob;
	}

	final public void setServiceMode (final SERVICE_MODE mode) {
		Debug.checkNull("SERVICE_MODE", mode);
		this.mode = mode;
	}

	final public SERVICE_MODE getServiceMode () {
		return this.mode;
	}

	void loadReportsFromCache (final FileFilter filter) {
		final File cache = this.getCache();
		if (cache == null) {
			return;
		}
		ChildrenList list = null;
		try {
			list = cache.listDirectChildren(filter);
		} catch (final IOException e) {
			e.printStackTrace();
			Err.reportError(e);
		}
		for (final File file : list) {
			this.loadCrashReport(file);
		}
	}

	private void loadCrashReport (final File file) {
		final ReportHandler report = new ReportHandler();

		final boolean success = report.readFromFile(file);
		if (success) {
			this.submitReport(report);
		}

	}

	private void submitReport (final ReportHandler report) {
		if (report.data == null) {
			Err.reportError("Report data is null");
		} else {
			this.queue.add(report);
		}
	}

}
