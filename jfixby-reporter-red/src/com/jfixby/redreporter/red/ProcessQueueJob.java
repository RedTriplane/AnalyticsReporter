
package com.jfixby.redreporter.red;

import com.jfixby.cmns.api.math.IntegerMath;
import com.jfixby.cmns.api.sys.Sys;
import com.jfixby.cmns.api.taskman.Job;

public class ProcessQueueJob implements Job {

	private static final long MAX_SLEEP = 1000 * 60 * 60 * 4;// 4 hours
	private final ReportsQueue reportsQueue;

	public ProcessQueueJob (final ReportsQueue reportsQueue) {
		this.reportsQueue = reportsQueue;
	}

	@Override
	public void doStart () throws Throwable {
		this.resetSleep();
	}

	private void resetSleep () {
		this.sleepAttempt = 0;
		this.sleepBase = 1000;
		this.sleepGrowth = 2;
		this.lastSleep = 0;
	}

	@Override
	public void doPush () throws Throwable {
		final RedReport report = this.reportsQueue.peek();
		if (report == null) {
			Sys.sleep(this.sleep());
			return;
		}
		final boolean success = this.reportsQueue.tryToSendReport(report);
		if (!success) {
			Sys.sleep(this.sleep());
			return;
		}
		this.reportsQueue.remove();
		this.resetSleep();
// Sys.sleep(this.sleep());
		return;
	}

	long sleepAttempt = 0;
	long sleepBase = 1000;
	long sleepGrowth = 2;
	long lastSleep = 0;

	private long sleep () {
		this.sleepAttempt++;
		this.lastSleep = this.lastSleep * this.sleepGrowth + this.sleepBase;
		this.lastSleep = IntegerMath.limit(this.sleepBase, this.lastSleep, MAX_SLEEP);
		return this.lastSleep;
	}

	@Override
	public boolean isDone () {
		return false;
	}

}
