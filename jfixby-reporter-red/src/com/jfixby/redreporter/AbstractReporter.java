
package com.jfixby.redreporter;

import java.io.IOException;

import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.err.Err;
import com.jfixby.cmns.api.file.File;
import com.jfixby.redreporter.api.analytics.SERVICE_MODE;
import com.jfixby.redreporter.api.transport.ReporterTransport;

public class AbstractReporter {
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

	final public void startService () {
	}

	final public void stopService () {
	}

	final public void setServiceMode (final SERVICE_MODE mode) {
		Debug.checkNull("SERVICE_MODE", mode);
		this.mode = mode;
	}

	final public SERVICE_MODE getServiceMode () {
		return this.mode;
	}

}
