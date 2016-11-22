
package com.jfixby.redreporter.client;

import com.jfixby.cmns.api.collections.Collection;
import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.Set;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.net.http.HttpURL;

public class ClientConfig {

	File reportingCache;
	final Set<HttpURL> servers = Collections.newSet();
	private boolean log;
	private boolean err;

	public File getReportingCache () {
		return this.reportingCache;
	}

	public void addAnalyticsServerUrl (final HttpURL url) {
		this.servers.add(url);
	}

	public Collection<HttpURL> listServers () {
		return this.servers;
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
