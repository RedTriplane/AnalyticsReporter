
package com.jfixby.redreporter.red;

import com.jfixby.cmns.api.taskman.Job;

public class ProcessQueueJob implements Job {

	private final ReportsQueue reportsQueue;

	public ProcessQueueJob (final ReportsQueue reportsQueue) {
		this.reportsQueue = reportsQueue;
	}

	@Override
	public void doStart () throws Throwable {
	}

	@Override
	public void doPush () throws Throwable {
		if (this.reportsQueue.isOperatingWithCache()) {
			this.reportsQueue.cacheNonCached();
		}

		return;
	}

	@Override
	public boolean isDone () {
		return false;
	}

}
