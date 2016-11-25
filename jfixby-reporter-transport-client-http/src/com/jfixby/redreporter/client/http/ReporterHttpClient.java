
package com.jfixby.redreporter.client.http;

import com.jfixby.cmns.api.collections.Collection;
import com.jfixby.cmns.api.collections.Mapping;
import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.net.http.HttpURL;
import com.jfixby.cmns.api.net.message.Message;
import com.jfixby.cmns.api.sys.SystemInfo;
import com.jfixby.redreporter.api.DeviceRegistration;
import com.jfixby.redreporter.api.transport.REPORTER_PROTOCOL;
import com.jfixby.redreporter.api.transport.ReporterTransportComponent;

public class ReporterHttpClient implements ReporterTransportComponent {
	final ServerHandlers servers = new ServerHandlers();

	public ReporterHttpClient (final ReporterHttpClientConfig config) {
		Debug.checkNull("config", config);

		final Collection<HttpURL> urls = config.listServers();
		Debug.checkTrue("no analytics servers provided", urls.size() > 0);
		for (final HttpURL url : urls) {
			final ServerHandler handler = new ServerHandler(url);
			this.servers.add(handler);
		}
	}

	@Override
	public DeviceRegistration registerDevice (final SystemInfo deviceInfo) {

		final Mapping<String, String> params = deviceInfo.listParameters();

		final Message request = new Message(REPORTER_PROTOCOL.REGISTER_DEVICE);
		request.values.putAll(params.toJavaMap());

		final Message response = exchange(this.servers, request);

		final HttpDeviceRegistration reg = new HttpDeviceRegistration();

		response.print();

		return reg;
	}

	public static Message exchange (final ServerHandlers servers, final Message request) {
		for (final ServerHandler server : servers) {
			final Message response = server.exchange(request);
			if (response != null) {
				return response;
			}
		}
		return null;
	}

	public void updatePings () {
		this.servers.updatePings();
	}

	public void printPings () {
		this.servers.printPings();
	}

}
