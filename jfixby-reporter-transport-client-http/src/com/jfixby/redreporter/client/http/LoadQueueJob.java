
package com.jfixby.redreporter.client.http;

import com.jfixby.cmns.api.file.ChildrenList;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.taskman.Job;

public class LoadQueueJob implements Job {

	private final File cache;
	private final CachedFilesFilter fileFilter;
	private final ReportsQueue reportsQueue;

	public LoadQueueJob (final ReportsQueue reportsQueue) {
		this.cache = reportsQueue.getCache();
		this.fileFilter = reportsQueue.cashed_files_filter;
		this.reportsQueue = reportsQueue;
	}

	@Override
	public void doStart () throws Throwable {
		final ChildrenList logs = this.cache.listDirectChildren(this.fileFilter);
		for (final File file : logs) {
			final RedReport report = RedReport.readFromFile(file);
			if (report != null) {
				this.reportsQueue.submit(report);
			} else {

			}
		}
	}

	@Override
	public void doPush () throws Throwable {
	}

	@Override
	public boolean isDone () {
		return true;
	}

}
