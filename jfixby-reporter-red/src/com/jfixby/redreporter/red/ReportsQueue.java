
package com.jfixby.redreporter.red;

import java.util.LinkedList;

import com.jfixby.cmns.api.err.Err;
import com.jfixby.redreporter.api.transport.ReporterTransport;

public class ReportsQueue {

	final LinkedList<RedReport> cachedReportsQ = new LinkedList<RedReport>();
	final LinkedList<RedReport> nonCachedReportsQueue = new LinkedList<RedReport>();

	final ProcessQueueJob processQueueJob = new ProcessQueueJob(this);
	private final RedReporter redReporter;

	public ReportsQueue (final RedReporter redReporter) {
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
		final boolean success = ReporterTransport.sendReport(reportsQueue);
		return success;
	}

	public ProcessQueueJob getProcessQueueJob () {
		return this.processQueueJob;
	}

	public boolean isOperatingWithCache () {
		return this.redReporter.cacheFolderState == RedReporter.CACHE_FOLDER_SUCCESSFULLY_CREATED;
	}

}
