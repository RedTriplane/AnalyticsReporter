
package com.jfixby.redreporter;

import java.io.IOException;

import com.jfixby.cmns.api.assets.ID;
import com.jfixby.cmns.api.assets.Names;
import com.jfixby.cmns.api.collections.Collection;
import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.List;
import com.jfixby.cmns.api.collections.Map;
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
import com.jfixby.redreporter.api.analytics.AnalyticsReporterComponent;
import com.jfixby.redreporter.api.transport.REPORTER_PROTOCOL;
import com.jfixby.redreporter.api.transport.ReporterTransport;

public abstract class RedAnalyticsReporter implements AnalyticsReporterComponent {

	private static final boolean OK = true;
	private final File logsCache;
	private final ReporterTransport transport;
	private boolean cacheIsValid;

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

	final private long period = 100;
	final private long period_long = 5000;

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

	private boolean loadCrashReport (final File file) {
		final RedReport report = RedReport.readFromCache(file);
		if (report == null) {
			return false;
		}
		this.submitReport(report);
		return true;

	}

	public RedReport newReport () {
		final RedReport report = new RedReport();
		return report;
	}

	private void submitReport (final RedReport report) {
		this.queue.add(report);

	}

	@Override
	public String toString () {
		return "RedAnalyticsReporter[" + this.serviceID + "]";
	}

	public RedAnalyticsReporter (final ReporterTransport transport, final File logsCache) {
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
		L.d("serviceID", this.serviceID);
	}

	protected static final String LOGS_FILE_NAME_SUFFIX = ".debug.log";
	private final ID serviceID = Names.newID("com.red-triplane.reporter.analytics");

	private final FileFilter log_files_filter = new FileFilter() {

		@Override
		public boolean fits (final File element) {
			return element.getName().endsWith(LOGS_FILE_NAME_SUFFIX);
		}
	};

	void loadReportsFromCache () {
		this.loadReportsFromCache(this.log_files_filter);
	}

	String getLogFileExtention () {
		return LOGS_FILE_NAME_SUFFIX;
	}

	final Map<String, String> params = Collections.newMap();

	Mapping<String, String> onTryToSendReport (final RedReport report) {
		this.params.put(REPORTER_PROTOCOL.SERVICE_ID, this.serviceID + "");
		return this.params;
	}

}
