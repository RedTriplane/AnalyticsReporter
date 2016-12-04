
package com.jfixby.redreporter.client.http;

import java.io.IOException;

import com.jfixby.cmns.api.collections.Collection;
import com.jfixby.cmns.api.collections.Mapping;
import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.err.Err;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.java.ByteArray;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.net.http.HttpURL;
import com.jfixby.cmns.api.net.message.Message;
import com.jfixby.redreporter.api.analytics.Report;
import com.jfixby.redreporter.api.transport.REPORTER_PROTOCOL;
import com.jfixby.redreporter.api.transport.ReporterTransport;
import com.jfixby.redreporter.api.transport.ServersCheck;
import com.jfixby.redreporter.api.transport.ServersCheckParams;

public class ReporterHttpClient implements ReporterTransport {

	final ServerHandlers servers = new ServerHandlers();
	private final InstallationIDStorage iidStorage;
	private final File cacheFolder;
	private final ReportsQueue queue;

	public ReporterHttpClient (final ReporterHttpClientConfig config) {
		Debug.checkNull("config", config);

		this.cacheFolder = config.getCacheFolder();
		try {
			this.cacheFolder.checkIsFolder();
		} catch (final IOException e) {
			Err.reportError(e);
		}
		this.queue = new ReportsQueue(this.cacheFolder);

		final Collection<HttpURL> urls = config.listServers();
		final File iidStorage;
		iidStorage = config.getInstallationIDStorageFolder();

		this.iidStorage = new InstallationIDStorage(iidStorage, config.getIIDFileName());

		Debug.checkTrue("no analytics servers provided", urls.size() > 0);
		for (final HttpURL url : urls) {
			final ServerHandler handler = new ServerHandler(url);
			this.servers.add(handler);
		}
	}

// public InstallationID registerInstallation (final SystemInfo systemInfo) {
//
// final Mapping<String, String> params = systemInfo.listParameters();
//
// final Message request = new Message(REPORTER_PROTOCOL.REGISTER_INSTALLATION);
// request.values.putAll(params.toJavaMap());
//
// final Message response = exchange(this.servers, request);
// if (response == null) {
// return null;
// }
//
//// response.print();
//
// final String token = response.values.get(REPORTER_PROTOCOL.INSTALLATION_TOKEN);
// if (token == null) {
// return null;
// }
// final InstallationID reg = new InstallationID(token);
// return reg;
// }

	public Message exchange (final ServerHandlers servers, final Message request) {
		final Message response = null;
		this.checkToken(response);
		L.e("exchange: not implemented yet");
// for (final ServerHandler server : servers) {
// final Message response = server.exchange(request);
// if (response != null) {
// return response;
// } else {
// L.d(" exchange failed", server);
// }
// }
		return null;
// return request;
	}

	public boolean sendReport (final Report report, final Mapping<String, String> params) {
		final Message message = new Message(REPORTER_PROTOCOL.REPORT);
		this.packToMessage(report, params, message);
		final Message response = this.exchange(this.servers, message);
		if (response == null) {
			return false;
		}
		if (!REPORTER_PROTOCOL.REPORT_RECEIVED_OK.equals(response.header)) {
			return false;
		}

		return true;
	}

	private void checkToken (final Message response) {
		final String token = response.values.get(REPORTER_PROTOCOL.INSTALLATION_TOKEN);
		if (token != null) {
			this.iidStorage.setID(token);
		}
	}

	private void packToMessage (final Report report, final Mapping<String, String> params, final Message message) {
		final ByteArray data = report.getPackedData();
		message.attachments.put(REPORTER_PROTOCOL.REPORT, data.toArray());
		if (params != null) {
			message.values.putAll(params.toJavaMap());
		}
	}

	public ServersCheck checkServers (final ServersCheckParams params) {
		return this.servers.checkAll(params);
	}

	public ServersCheckParams newServersCheckParams () {
		return new RedServersCheckParams();
	}

	public ServersCheck checkServers () {
		return this.checkServers(new RedServersCheckParams());
	}

	@Override
	public boolean resetInstallationID () {
		return this.iidStorage.deleteID();
	}

	@Override
	public boolean submitReport (final Report report) {
		return false;
	}

}
