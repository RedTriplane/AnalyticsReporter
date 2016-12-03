
package com.jfixby.redreporter;

import java.io.IOException;

import com.jfixby.cmns.api.assets.ID;
import com.jfixby.cmns.api.assets.Names;
import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.Map;
import com.jfixby.cmns.api.collections.Mapping;
import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.err.Err;
import com.jfixby.cmns.api.file.ChildrenList;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.file.FileFilter;
import com.jfixby.cmns.api.log.L;
import com.jfixby.redreporter.api.crash.CrashReporterComponent;
import com.jfixby.redreporter.api.transport.REPORTER_PROTOCOL;
import com.jfixby.redreporter.api.transport.ReporterTransport;

public abstract class RedCrashReporter implements CrashReporterComponent {

	@Override
	public String toString () {
		return "RedCrashReporter[" + this.serviceID + "]";
	}

	protected static final String CRASH_FILE_NAME_SUFFIX = ".crash.log";
	private final ID serviceID = Names.newID("com.red-triplane.reporter.crash");
	private static final boolean OK = true;
	private final File logsCache;
	private final ReporterTransport transport;
	private boolean cacheIsValid;
	final ReportsQueue queue = new ReportsQueue();

	public RedCrashReporter (final ReporterTransport transport, final File logsCache) {
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

		L.d("serviceID", this.serviceID);
	}

	final RedReporterUncaughtExceptionHandler uncaughtExceptionHandler = new RedReporterUncaughtExceptionHandler(this);
	final RedReporterErrorsListener errorsListener = new RedReporterErrorsListener(this);
	final RedReporterLoggerListener logsListener = new RedReporterLoggerListener(this);
	private final FileFilter crash_files_filter = new FileFilter() {

		@Override
		public boolean fits (final File element) {
			return element.getName().endsWith(CRASH_FILE_NAME_SUFFIX);
		}
	};

	void loadReportsFromCache () {
		this.loadReportsFromCache(this.crash_files_filter);
	}

	String getLogFileExtention () {
		return CRASH_FILE_NAME_SUFFIX;
	}

	final Map<String, String> params = Collections.newMap();

	Mapping<String, String> onTryToSendReport (final RedReport report) {
		this.params.put(REPORTER_PROTOCOL.SERVICE_ID, this.serviceID + "");
		return this.params;
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

	void loadReportsFromCache (final FileFilter filter) {
		int k = 0;
		final File cache = this.getCache();
		if (cache == null) {
			return;
		}
		ChildrenList list = null;
		try {
			list = cache.listDirectChildren(filter);
		} catch (final IOException e) {
			e.printStackTrace();
			Err.reportError(e);
		}
		for (final File file : list) {
			if (this.loadCrashReport(file)) {
				k++;
			}
		}

		L.d("CrashReports awaiting in queue", this.queue.size());
	}

	private boolean loadCrashReport (final File file) {
		final RedReport report = RedReport.readFromCache(file);
		if (report == null) {
			return false;
		}
		this.submitReport(report);
		return true;
	}

	public RedReport newReport () {
		final RedReport report = new RedReport();
		return report;
	}

	private void submitReport (final RedReport report) {
		Debug.checkNull("report", report);
		this.queue.add(report);
	}

	@Override
	public void enableUncaughtExceptionHandler () {
		this.uncaughtExceptionHandler.enable();
	}

	@Override
	public void enableErrorsListener () {
		this.errorsListener.enable();
	}

	@Override
	public void enableLogsListener () {
		this.logsListener.enable();
	}

	@Override
	public void disableUncaughtExceptionHandler () {
		this.uncaughtExceptionHandler.disable();
	}

	@Override
	public void disableErrorsListener () {
		this.errorsListener.disable();
	}

	@Override
	public void disableLogsListener () {
		this.logsListener.disable();
	}

	@Override
	public boolean isUncaughtExceptionHandlerEnabled () {
		return this.uncaughtExceptionHandler.isEnabled();
	}

	@Override
	public boolean isErrorsListenerEnabled () {
		return this.errorsListener.isEnabled();
	}

	@Override
	public boolean isLogsListenerEnabled () {
		return this.logsListener.isEnabled();
	}

}
