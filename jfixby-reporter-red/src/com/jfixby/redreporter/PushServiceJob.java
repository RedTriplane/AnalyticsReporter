
package com.jfixby.redreporter;

import com.jfixby.cmns.api.taskman.Job;

public class PushServiceJob implements Job {

	private final AbstractReporter abstractReporter;

	public PushServiceJob (final AbstractReporter abstractReporter) {
		this.abstractReporter = abstractReporter;
	}

	@Override
	public void doStart () throws Throwable {
		this.abstractReporter.start();
	}

	@Override
	public void doPush () throws Throwable {
		this.abstractReporter.push();
	}

	@Override
	public boolean isDone () {
		if (this.abstractReporter.stopJob) {
			this.abstractReporter.serviceIsStopping = false;
		}
		return this.abstractReporter.stopJob;
	}

}
