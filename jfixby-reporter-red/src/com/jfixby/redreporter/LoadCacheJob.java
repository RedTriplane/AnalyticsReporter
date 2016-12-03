
package com.jfixby.redreporter;

import com.jfixby.cmns.api.taskman.Job;

public class LoadCacheJob implements Job {

	private final AbstractReporter abstractReporter;

	public LoadCacheJob (final AbstractReporter abstractReporter) {
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
