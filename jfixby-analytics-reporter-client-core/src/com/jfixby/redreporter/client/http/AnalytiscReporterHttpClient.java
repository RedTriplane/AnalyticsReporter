
package com.jfixby.redreporter.client.http;

import com.jfixby.cmns.api.collections.Collection;
import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.net.http.HttpURL;
import com.jfixby.redreporter.api.AnalyticsReporterAPI;
import com.jfixby.redreporter.api.DeviceRegistration;

public class AnalytiscReporterHttpClient implements AnalyticsReporterAPI {
	final ServerHandlers servers = new ServerHandlers();

	public AnalytiscReporterHttpClient (final AnalytiscReporterHttpClientConfig config) {
		Debug.checkNull("config", config);

		final Collection<HttpURL> urls = config.listServers();
		Debug.checkTrue("no analytics servers provided", urls.size() > 0);
		for (final HttpURL url : urls) {
			final ServerHandler handler = new ServerHandler(url);
			this.servers.add(handler);
		}
	}

	@Override
	public DeviceRegistration registerDevice () {

		final HttpDeviceRegistration reg = new HttpDeviceRegistration();

		final boolean success = Registrator.registerDevice(this.servers, reg);
		if (!success) {
			return null;
		}
		return reg;
	}

	public void updatePings () {
		this.servers.updatePings();
	}

	public void printPings () {
		this.servers.printPings();
	}

}
