
package com.jfixby.redreporter;

import java.io.IOException;

import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.err.Err;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.sys.Sys;
import com.jfixby.cmns.api.taskman.Job;
import com.jfixby.redreporter.api.analytics.Report;
import com.jfixby.redreporter.api.analytics.SERVICE_MODE;
import com.jfixby.redreporter.api.transport.ReporterTransport;

public class AbstractReporter {
	private static final boolean OK = true;
	private final File logsCache;
	private final ReporterTransport transport;
	boolean cacheIsValid;
	private SERVICE_MODE mode;

	public AbstractReporter (final ReporterTransport transport, final File logsCache) {
		this.logsCache = Debug.checkNull("logsCache", logsCache);
		try {
			this.logsCache.makeFolder();
			this.cacheIsValid = true;
		} catch (final IOException e) {
			e.printStackTrace();
			Err.reportError(e);
			this.cacheIsValid = false;
		}
		this.transport = Debug.checkNull("transport", transport);
	}

	public File getCache () {
		if (!this.cacheIsValid) {
			return null;
		}
		return this.logsCache;
	}

	public ReporterTransport getTransport () {
		return this.transport;
	}

	final ReportsQueue queue = new ReportsQueue();

	private void start () {
		L.d("doStart", this);
		this.stopJob = false;
	}

	private final void push () {
		L.d("push", this);
		{
			// --------------------------------
			if (this.queue.size() == 0) {
				Sys.sleep(this.period);
				return;
			}
			final Report report = this.queue.peek();
			final boolean result = this.transport.sendReport(report);
			if (result == OK) {
				report.dispose();
				this.queue.remove();
				return;
			}

			this.queue.ensureCached();
			// --------------------------------
		}
		Sys.sleep(1000);
	}

	final private long period = 100;
	final Job serviceJob = new Job() {

		@Override
		public void doStart () throws Throwable {
			AbstractReporter.this.start();
		}

		@Override
		public void doPush () throws Throwable {
			AbstractReporter.this.push();
		}

		@Override
		public boolean isDone () {
			if (AbstractReporter.this.stopJob) {
				AbstractReporter.this.serviceIsStopping = false;
			}
			return AbstractReporter.this.stopJob;
		}
	};
	private boolean stopJob;
	boolean serviceIsStopping = false;

	public void requestServiceStop (final boolean wait) {
		this.serviceIsStopping = true;
		this.stopJob = true;
		while (wait && this.serviceIsStopping) {
			Sys.sleep(1);
		}
	}

	public Job getServiceJob () {
		return this.serviceJob;
	}

	final public void setServiceMode (final SERVICE_MODE mode) {
		Debug.checkNull("SERVICE_MODE", mode);
		this.mode = mode;
	}

	final public SERVICE_MODE getServiceMode () {
		return this.mode;
	}

}
