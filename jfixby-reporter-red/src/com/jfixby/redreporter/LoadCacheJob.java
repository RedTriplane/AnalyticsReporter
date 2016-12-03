
package com.jfixby.redreporter;

import com.jfixby.cmns.api.taskman.Job;

public class LoadCacheJob implements Job {

	private final RedAnalyticsReporter abstractReporter;

	public LoadCacheJob (final RedAnalyticsReporter abstractReporter) {
		this.abstractReporter = abstractReporter;
	}

	@Override
	public void doStart () throws Throwable {
		this.abstractReporter.loadReportsFromCache();
	}

	@Override
	public void doPush () throws Throwable {
	}

	@Override
	public boolean isDone () {
		return true;
	}
}
