
package com.jfixby.redreporter.client.http;

import com.jfixby.cmns.api.collections.Collection;
import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.Set;
import com.jfixby.cmns.api.net.http.HttpURL;

public class AnalytiscReporterHttpClientConfig {
	final Set<HttpURL> servers = Collections.newSet();

	public void addAnalyticsServerUrl (final HttpURL url) {
		this.servers.add(url);
// this.servers.print("+" + url);
	}

	public Collection<HttpURL> listServers () {
		return this.servers;
	}

}
