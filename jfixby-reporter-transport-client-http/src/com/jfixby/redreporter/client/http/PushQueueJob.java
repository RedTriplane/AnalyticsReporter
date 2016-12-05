
package com.jfixby.redreporter.client.http;

import com.jfixby.cmns.api.java.gc.GARBAGE_MODE;
import com.jfixby.cmns.api.java.gc.GCFisher;
import com.jfixby.cmns.api.sys.Sys;
import com.jfixby.cmns.api.taskman.Job;
import com.jfixby.cmns.api.time.TimeStream;

public class PushQueueJob implements Job {

	private final ReportsQueue reportsQueue;
	private final long period_long = TimeStream.SECOND * 1;

	public PushQueueJob (final ReportsQueue reportsQueue) {
		this.reportsQueue = reportsQueue;
	}

	@Override
	public void doStart () throws Throwable {
	}

	boolean queueProcessingIsComplete = false;

	@Override
	public void doPush () throws Throwable {
		if (GCFisher.isGarbageModeFlag(GARBAGE_MODE.GARBAGE_SAVING)) {
			Sys.component().sleep(this.period_long);
			return;
		}

		this.queueProcessingIsComplete = this.reportsQueue.tryToProcess();

	}

	@Override
	public boolean isDone () {
		return this.queueProcessingIsComplete;
	}

}
