
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
import com.jfixby.redreporter.api.analytics.Report;

public class ReportsQueue {

	private final LinkedList<Report> all = new LinkedList<Report>();
	private final LinkedHashSet<Report> nonCached = new LinkedHashSet<Report>();
	private final HashSet<Report> toRemove = new HashSet<Report>();

	final CachedFilesFilter cashed_files_filter = new CachedFilesFilter();

	private final File logsCache;
	private boolean cacheIsValid;

	public ReportsQueue (final File logsCache) {
		this.logsCache = Debug.checkNull("logsCache", logsCache);
		try {
			this.logsCache.makeFolder();
			this.cacheIsValid = true;
		} catch (final IOException e) {
			e.printStackTrace();
			Err.reportError(e);
			this.cacheIsValid = false;
		}
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
		final Report report = RedReport.readFromCache(file);
		if (report == null) {
			return false;
		}
		this.submit(report);
		return true;

	}

	void loadReportsFromCache () {
		this.loadReportsFromCache(this.cashed_files_filter);
	}

	void ensureCached (final String extention) {
		if (this.nonCached.size() == 0) {
			return;
		}
		this.toRemove.clear();
		for (final Iterator<Report> i = this.nonCached.iterator(); i.hasNext();) {
			final Report e = i.next();
			final boolean success = this.cache(e, this.getCache(), extention);
			if (success) {
				this.toRemove.add(e);
			}
		}
		this.nonCached.removeAll(this.toRemove);
		this.toRemove.clear();

	}

	private boolean cache (final Report e, final File cache, final String extention) {
		Err.reportNotImplementedYet();
		return false;
	}

	public synchronized void submit (final Report report) {
		this.all.add(report);
		this.nonCached.add(report);
	}
}
