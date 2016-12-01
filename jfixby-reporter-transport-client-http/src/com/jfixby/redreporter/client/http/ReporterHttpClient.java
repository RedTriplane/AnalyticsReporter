
package com.jfixby.redreporter.client.http;

import com.jfixby.cmns.api.collections.Collection;
import com.jfixby.cmns.api.collections.Mapping;
import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.err.Err;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.net.http.HttpURL;
import com.jfixby.cmns.api.net.message.Message;
import com.jfixby.cmns.api.sys.SystemInfo;
import com.jfixby.redreporter.api.InstallationID;
import com.jfixby.redreporter.api.analytics.Report;
import com.jfixby.redreporter.api.transport.REPORTER_PROTOCOL;
import com.jfixby.redreporter.api.transport.ReporterTransport;
import com.jfixby.redreporter.api.transport.ServersCheck;
import com.jfixby.redreporter.api.transport.ServersCheckParams;

public class ReporterHttpClient implements ReporterTransport {

	final ServerHandlers servers = new ServerHandlers();
	private final InstallationIDStorage storage;

	public ReporterHttpClient (final ReporterHttpClientConfig config) {
		Debug.checkNull("config", config);

		final Collection<HttpURL> urls = config.listServers();
		final File iidStorage;
		iidStorage = config.getInstallationIDStorageFolder();

		this.storage = new InstallationIDStorage(iidStorage, config.getIIDFileName());

		Debug.checkTrue("no analytics servers provided", urls.size() > 0);
		for (final HttpURL url : urls) {
			final ServerHandler handler = new ServerHandler(url);
			this.servers.add(handler);
		}
	}

	public InstallationID registerInstallation (final SystemInfo systemInfo) {

		final Mapping<String, String> params = systemInfo.listParameters();

		final Message request = new Message(REPORTER_PROTOCOL.REGISTER_INSTALLATION);
		request.values.putAll(params.toJavaMap());

		final Message response = exchange(this.servers, request);
		if (response == null) {
			return null;
		}

// response.print();

		final String token = response.values.get(REPORTER_PROTOCOL.INSTALLATION_TOKEN);
		if (token == null) {
			return null;
		}
		final InstallationID reg = new InstallationID(token);
		return reg;
	}

	public static Message exchange (final ServerHandlers servers, final Message request) {
		for (final ServerHandler server : servers) {
			final Message response = server.exchange(request);
			if (response != null) {
				return response;
			} else {
				L.d("  exchange failed", server);
			}
		}
		return null;
	}

	@Override
	public boolean sendReport (final Report report) {
		final Message message = new Message(REPORTER_PROTOCOL.REPORT);
		this.packToMessage(report, message);
		final Message response = exchange(this.servers, message);
		if (response == null) {
			return false;
		}
		return true;
	}

	private void packToMessage (final Report report, final Message message) {
		Err.reportNotImplementedYet();
	}

	@Override
	public ServersCheck checkServers (final ServersCheckParams params) {
		return this.servers.checkAll(params);

	}

	@Override
	public ServersCheckParams newServersCheckParams () {
		return new RedServersCheckParams();
	}

	@Override
	public ServersCheck checkServers () {
		return this.checkServers(new RedServersCheckParams());
	}

	@Override
	public boolean resetInstallationID () {
		return this.storage.deleteID();
	}

}
