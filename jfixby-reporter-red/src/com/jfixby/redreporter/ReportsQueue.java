
package com.jfixby.redreporter;

import java.util.LinkedList;

import com.jfixby.cmns.api.err.Err;
import com.jfixby.redreporter.crash.RedCrashReporter;

public class ReportsQueue {

	final LinkedList<RedReport> cachedReportsQ = new LinkedList<RedReport>();
	final LinkedList<RedReport> nonCachedReportsQueue = new LinkedList<RedReport>();

	private final RedCrashReporter redReporter;

	public ReportsQueue (final RedCrashReporter redReporter) {
		this.redReporter = redReporter;
	}

	public synchronized void submit (final RedReport redReport) {

		this.nonCachedReportsQueue.add(redReport);

	}

	synchronized void cacheNonCached () {
		while (this.nonCachedReportsQueue.size() > 0) {
			final RedReport next = this.nonCachedReportsQueue.removeFirst();
			this.cache(next);
			this.cachedReportsQ.add(next);
		}
	}

	private void cache (final RedReport next) {
		Err.reportNotImplementedYet();
	}

	public boolean tryToSendReport (final RedReport reportsQueue) {
		Err.reportError("");
		return false;
	}

}
