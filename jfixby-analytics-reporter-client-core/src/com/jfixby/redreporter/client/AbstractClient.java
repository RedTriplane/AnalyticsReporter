
package com.jfixby.redreporter.client;

import java.io.IOException;

import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.file.File;
import com.jfixby.redreporter.api.RedReporterComponent;

public class AbstractClient implements RedReporterComponent {

	private final File reportingCache;

	public AbstractClient (final File reportingCache) throws IOException {
		Debug.checkNull("reportingCache", reportingCache);
		Debug.checkTrue("reportingCache is file", !reportingCache.isFile());
		reportingCache.makeFolder();
		this.reportingCache = reportingCache;
	}

}
