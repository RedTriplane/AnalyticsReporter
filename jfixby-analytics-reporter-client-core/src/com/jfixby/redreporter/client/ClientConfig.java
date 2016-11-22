
package com.jfixby.redreporter.client;

import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.net.http.HttpURL;

public class ClientConfig {

	File reportingCache;
	private HttpURL server_url;
	private boolean log;
	private boolean err;

	public File getReportingCache () {
		return this.reportingCache;
	}

	public void setAnalyticsServerUrl (final HttpURL url) {
		this.server_url = url;
	}

	public HttpURL getAnalyticsServerUrl () {
		return this.server_url;
	}

	public void setReportingCache (final File cache) {
		this.reportingCache = cache;
	}

	public void setWrapCurrentLogger (final boolean b) {
		this.log = b;
	}

	public void setWrapCurrentErr (final boolean b) {
		this.err = b;
	}

	public boolean wrapCurrentLogger () {
		return this.log;
	}

	public boolean wrapCurrentErr () {
		return this.err;
	}

}
