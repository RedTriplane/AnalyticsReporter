
package com.jfixby.redreporter;

import java.io.IOException;

import com.jfixby.cmns.api.collections.Collection;
import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.List;
import com.jfixby.cmns.api.collections.Mapping;
import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.err.Err;
import com.jfixby.cmns.api.file.ChildrenList;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.file.FileFilter;
import com.jfixby.cmns.api.java.gc.GARBAGE_MODE;
import com.jfixby.cmns.api.java.gc.GCFisher;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.sys.Sys;
import com.jfixby.cmns.api.taskman.Job;
import com.jfixby.redreporter.api.analytics.Report;
import com.jfixby.redreporter.api.transport.ReporterTransport;

public abstract class AbstractReporter {
	private static final boolean OK = true;
	private final File logsCache;
	private final ReporterTransport transport;
	private boolean cacheIsValid;

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

	void start () {
		L.d("doStart", this);
		this.stopJob = false;
	}

	final void push () {
// L.d("push", this);
		{
			// --------------------------------
			if (GCFisher.isGarbageModeFlag(GARBAGE_MODE.GARBAGE_SAVING)) {
				Sys.sleep(this.period_long);
				return;
			}
			if (this.queue.size() == 0) {
				Sys.sleep(this.period);
				return;
			}
			final RedReport report = this.queue.peek();
			final boolean result = this.transport.sendReport(report, this.onTryToSendReport(report));
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

	abstract Mapping<String, String> onTryToSendReport (RedReport report);

	final private long period = 100;
	final private long period_long = 5000;

	abstract void loadReportsFromCache ();

	final LoadCacheJob loadCache = new LoadCacheJob(this);
	final PushServiceJob pushService = new PushServiceJob(this);

	final List<Job> serviceJob = Collections.newList(this.loadCache, this.pushService);

	boolean stopJob;
	boolean serviceIsStopping = false;

	public void requestServiceStop (final boolean wait) {
		this.serviceIsStopping = true;
		this.stopJob = true;
		while (wait && this.serviceIsStopping) {
			Sys.sleep(30);
		}
	}

	public Collection<Job> getServiceJob () {
		return this.serviceJob;
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
		final RedReport report = RedReport.readFromCache(file);

		this.submitReport(report);

	}

	public Report newReport () {
		final RedReport report = new RedReport();
		return report;
	}

	private void submitReport (final RedReport report) {
		this.queue.add(report);

	}

}
