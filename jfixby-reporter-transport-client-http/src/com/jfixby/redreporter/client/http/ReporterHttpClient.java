
package com.jfixby.redreporter.client.http;

import com.jfixby.cmns.api.collections.Collection;
import com.jfixby.cmns.api.collections.Mapping;
import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.err.Err;
import com.jfixby.cmns.api.net.http.HttpURL;
import com.jfixby.cmns.api.net.message.Message;
import com.jfixby.cmns.api.sys.SystemInfo;
import com.jfixby.redreporter.api.InstallationID;
import com.jfixby.redreporter.api.Report;
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
	public InstallationID registerInstallation (final SystemInfo systemInfo) {

		final Mapping<String, String> params = systemInfo.listParameters();

		final Message request = new Message(REPORTER_PROTOCOL.REGISTER_INSTALLATION);
		request.values.putAll(params.toJavaMap());

		final Message response = exchange(this.servers, request);
		if (response == null) {
			return null;
		}

		response.print();

		final String token = response.values.get(REPORTER_PROTOCOL.INSTALLATION_TOKEN);
		final InstallationID reg = new InstallationID();
		reg.token = token;
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

	@Override
	public boolean sendReport (final Report report) {
		final Message message = new Message(REPORTER_PROTOCOL.REPORT);
		this.packToMessage(report, message);
		final Message response = exchange(this.servers, message);
		if (response == null) {
			return false;
		}
		response.print();

		return true;
	}

	private void packToMessage (final Report report, final Message message) {
		Err.reportNotImplementedYet();
	}

	@Override
	public void pingServers () {
		this.servers.updatePings();
		this.servers.updatePings();
		this.servers.printPings();
	}

}
