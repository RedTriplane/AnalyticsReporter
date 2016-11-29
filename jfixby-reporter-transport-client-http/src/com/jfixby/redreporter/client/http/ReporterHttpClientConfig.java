
package com.jfixby.redreporter.client.http;

import com.jfixby.cmns.api.collections.Collection;
import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.Set;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.net.http.HttpURL;

public class ReporterHttpClientConfig {
	final Set<HttpURL> servers = Collections.newSet();
	private File iidStorage;

	public void addAnalyticsServerUrl (final HttpURL url) {
		this.servers.add(url);
// this.servers.print("+" + url);
	}

	public Collection<HttpURL> listServers () {
		return this.servers;
	}

	public void setInstallationIDStorageFolder (final File iidStorage) {
		this.iidStorage = iidStorage;
	}

	public File getInstallationIDStorageFolder () {
		return this.iidStorage;
	}

}
