
package com.jfixby.redreporter.red;

import java.util.LinkedList;

import com.jfixby.cmns.api.collections.Collection;
import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.List;
import com.jfixby.cmns.api.taskman.Job;
import com.jfixby.redreporter.api.transport.ReporterTransport;

public class ReportsQueue {

	final LinkedList<RedReport> queue = new LinkedList<RedReport>();
	private final List<Job> jobs = Collections.newList();
	private final GetTokenJob getToken = new GetTokenJob();
	private final ProcessQueueJob processQueueJob = new ProcessQueueJob(this);
	private final RedReporter redReporter;

	public ReportsQueue (final RedReporter redReporter) {
		this.redReporter = redReporter;
		this.jobs.add(this.getToken);
		this.jobs.add(this.processQueueJob);
	}

	public synchronized void submit (final RedReport redReport) {
		this.queue.add(redReport);
	}

	public synchronized RedReport peek () {
		return this.queue.peek();
	}

	public synchronized RedReport remove () {
		return this.queue.removeFirst();
	}

	public Collection<Job> getJob () {
		return this.jobs;
	}

	public boolean tryToSendReport (final RedReport reportsQueue) {
		final boolean success = ReporterTransport.sendReport(reportsQueue);
		return success;
	}

}
