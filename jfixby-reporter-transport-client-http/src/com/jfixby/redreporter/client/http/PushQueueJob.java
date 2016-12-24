
package com.jfixby.redreporter.client.http;

import com.jfixby.scarabei.api.java.gc.GARBAGE_MODE;
import com.jfixby.scarabei.api.java.gc.GCFisher;
import com.jfixby.scarabei.api.math.IntegerMath;
import com.jfixby.scarabei.api.sys.Sys;
import com.jfixby.scarabei.api.taskman.Job;
import com.jfixby.scarabei.api.time.TimeStream;

public class PushQueueJob implements Job {

	private static final long MAX_SLEEP_TIME = TimeStream.MINUTE * 40;
	private final ReportsQueue reportsQueue;
	private final long gc_sleep = TimeStream.SECOND * 1;

	public PushQueueJob (final ReportsQueue reportsQueue) {
		this.reportsQueue = reportsQueue;
	}

	@Override
	public void doStart () throws Throwable {
		this.failCount = 0;
	}

	boolean queueProcessingIsComplete = false;
	private int failCount;

	@Override
	public void doPush () throws Throwable {
		if (GCFisher.isGarbageModeFlag(GARBAGE_MODE.GARBAGE_SAVING)) {
			Sys.component().sleep(this.gc_sleep);
			return;
		}

		this.queueProcessingIsComplete = this.reportsQueue.tryToProcess();
		if (this.queueProcessingIsComplete) {
			this.failCount = 0;
			return;
		}
		this.failCount++;

		Sys.sleep(sleepOnFailTime(this.failCount));

	}

	static private long sleepOnFailTime (final int failCount) {
		if (failCount == 0) {
			return 0L;
		}
		if (failCount == 1) {
			return TimeStream.SECOND;
		}
		return IntegerMath.limit(0, failCount * failCount * TimeStream.SECOND, MAX_SLEEP_TIME);
	}

	@Override
	public boolean isDone () {
		return this.queueProcessingIsComplete;
	}

}
