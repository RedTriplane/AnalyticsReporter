
package com.jfixby.redreporter;

import com.jfixby.cmns.api.collections.Mapping;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.file.FileFilter;
import com.jfixby.redreporter.api.analytics.AnalyticsReporterComponent;
import com.jfixby.redreporter.api.transport.ReporterTransport;

public abstract class RedAnalyticsReporter extends AbstractReporter implements AnalyticsReporterComponent {

	public RedAnalyticsReporter (final ReporterTransport transport, final File logsCache) {
		super(transport, logsCache);
	}

	protected static final String LOGS_FILE_NAME_SUFFIX = ".debug.log";

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

	@Override
	Mapping<String, String> onTryToSendReport (final RedReport report) {
		return null;
	}

}
