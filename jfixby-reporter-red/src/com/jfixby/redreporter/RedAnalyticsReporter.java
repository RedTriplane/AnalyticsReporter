
package com.jfixby.redreporter;

import com.jfixby.cmns.api.assets.ID;
import com.jfixby.cmns.api.assets.Names;
import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.Map;
import com.jfixby.cmns.api.collections.Mapping;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.file.FileFilter;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.sys.Sys;
import com.jfixby.redreporter.api.analytics.AnalyticsReporterComponent;
import com.jfixby.redreporter.api.transport.REPORTER_PROTOCOL;
import com.jfixby.redreporter.api.transport.ReporterTransport;

public abstract class RedAnalyticsReporter extends AbstractReporter implements AnalyticsReporterComponent {

	public RedAnalyticsReporter (final ReporterTransport transport, final File logsCache) {
		super(transport, logsCache);
		L.d("serviceID", this.serviceID);
		Sys.exit();
	}

	protected static final String LOGS_FILE_NAME_SUFFIX = ".debug.log";
	private final ID serviceID = Names.newID("com.red-triplane.reporter.analytics");

	private final FileFilter log_files_filter = new FileFilter() {

		@Override
		public boolean fits (final File element) {
			return element.getName().endsWith(LOGS_FILE_NAME_SUFFIX);
		}
	};

	@Override
	void loadReportsFromCache () {
		this.loadReportsFromCache(this.log_files_filter);
	}

	@Override
	String getLogFileExtention () {
		return LOGS_FILE_NAME_SUFFIX;
	}

	final Map<String, String> params = Collections.newMap();

	@Override
	Mapping<String, String> onTryToSendReport (final RedReport report) {
		this.params.put(REPORTER_PROTOCOL.SERVICE_ID, this.serviceID + "");
		return this.params;
	}

}
