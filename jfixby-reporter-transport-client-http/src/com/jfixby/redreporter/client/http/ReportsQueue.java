
package com.jfixby.redreporter.client.http;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.err.Err;
import com.jfixby.cmns.api.file.ChildrenList;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.file.FileFilter;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.taskman.TASK_TYPE;
import com.jfixby.cmns.api.taskman.Task;
import com.jfixby.cmns.api.taskman.TaskManager;
import com.jfixby.cmns.api.taskman.TaskSpecs;
import com.jfixby.redreporter.api.report.Report;

public class ReportsQueue {

	private final LinkedList<Report> all = new LinkedList<Report>();
	private final LinkedHashSet<Report> nonCached = new LinkedHashSet<Report>();
	private final HashSet<Report> toRemove = new HashSet<Report>();

	final CachedFilesFilter cashed_files_filter = new CachedFilesFilter();
	final ObtainTokenJob obtainToken;
	final LoadQueueJob loadQueue;
	final PushQueueJob pushQueue;
	private final File logsCache;
	private boolean cacheIsValid;
	private Task task;
	private final ReporterHttpClient master;
	private final TASK_TYPE taskType;

	public ReporterHttpClient getTransport () {
		return this.master;
	}

	public ReportsQueue (final ReporterHttpClient reporterHttpClient, final File logsCache, final TASK_TYPE taskType) {
		this.logsCache = Debug.checkNull("logsCache", logsCache);
		this.taskType = Debug.checkNull("taskType", taskType);
		try {
			this.logsCache.makeFolder();
			this.cacheIsValid = true;
		} catch (final IOException e) {
			e.printStackTrace();
			Err.reportError(e);
			this.cacheIsValid = false;
		}
		this.master = reporterHttpClient;

		this.loadQueue = new LoadQueueJob(this);
		this.pushQueue = new PushQueueJob(this);
		this.obtainToken = new ObtainTokenJob(this);

	}

	File getCache () {
		if (!this.cacheIsValid) {
			return null;
		}
		return this.logsCache;
	}

	int size () {
		return this.all.size();
	}

	Report peek () {
		return this.all.peek();
	}

	Report remove () {
		final Report first = this.all.removeFirst();
		return first;
	}

	void loadReportsFromCache (final FileFilter filter) {
		int k = 0;
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
			if (this.loadCrashReport(file)) {
				k++;
			}
		}

		L.d("CrashReports awaiting in queue", this.size());
	}

	private boolean loadCrashReport (final File file) {
		final Report report = RedReport.readFromFile(file);
		if (report == null) {
			return false;
		}
		this.submit(report);
		return true;

	}

	void loadReportsFromCache () {
		this.loadReportsFromCache(this.cashed_files_filter);
	}

	void ensureCached () {
		if (this.nonCached.size() == 0) {
			return;
		}
		this.toRemove.clear();
		for (final Iterator<Report> i = this.nonCached.iterator(); i.hasNext();) {
			final Report e = i.next();
			final boolean success = e.ensureCached();
			if (success) {
				this.toRemove.add(e);
			}
		}
		this.nonCached.removeAll(this.toRemove);
		this.toRemove.clear();

	}

	public synchronized void submit (final Report report) {
		Debug.checkNull("report", report);
		this.all.add(report);
		this.nonCached.add(report);
		if (this.task != null) {
			return;
		}
		final TaskSpecs taskSpec = TaskManager.newTaskSpecs();
		taskSpec.setName("ReportsQueue::push");
		taskSpec.setType(this.taskType);
// taskSpec.addJob(this.loadQueue);
		taskSpec.addJob(this.obtainToken);
		taskSpec.addJob(this.pushQueue);
		this.task = TaskManager.newTask(taskSpec);
	}

	boolean cacheLoaded = false;

	public synchronized void loadFromCacheAndPush () {
		if (this.cacheLoaded) {
			Err.reportError("Cache is already loaded");
			return;
		}
		this.cacheLoaded = true;
		final TaskSpecs taskSpec = TaskManager.newTaskSpecs();
		taskSpec.setName("ReportsQueue::start");
		taskSpec.setType(this.taskType);
		taskSpec.addJob(this.obtainToken);
		taskSpec.addJob(this.loadQueue);
		taskSpec.addJob(this.pushQueue);
		this.task = TaskManager.newTask(taskSpec);
	}

	public synchronized boolean tryToProcess () {
		while (this.size() > 0) {
			final Report report = this.all.peek();
			final boolean success = this.master.tryToSend(report);
			if (success) {
				this.all.removeFirst();
				this.nonCached.remove(report);
				report.dispose();
			} else {
				this.ensureCached();
				return false;
			}
		}

		this.task = null;
		return true;
	}

}
