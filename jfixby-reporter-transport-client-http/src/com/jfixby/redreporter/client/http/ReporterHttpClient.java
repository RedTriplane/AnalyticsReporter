
package com.jfixby.redreporter.client.http;

import java.io.IOException;

import com.jfixby.cmns.api.collections.Collection;
import com.jfixby.cmns.api.collections.Mapping;
import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.err.Err;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.net.http.HttpURL;
import com.jfixby.cmns.api.net.message.Message;
import com.jfixby.cmns.api.sys.Sys;
import com.jfixby.cmns.api.sys.SystemInfo;
import com.jfixby.redreporter.api.InstallationID;
import com.jfixby.redreporter.api.analytics.Report;
import com.jfixby.redreporter.api.transport.REPORTER_PROTOCOL;
import com.jfixby.redreporter.api.transport.ReporterTransportComponent;
import com.jfixby.redreporter.api.transport.ServersCheck;
import com.jfixby.redreporter.api.transport.ServersCheckParams;

public class ReporterHttpClient implements ReporterTransportComponent {
	private static final String INSTALLATION_ID_FILE_NAME = "com.red-triplane.iid";
	final ServerHandlers servers = new ServerHandlers();
	private File iidStorage;
	InstallationID iid;
	private boolean iidSaved = false;

	public ReporterHttpClient (final ReporterHttpClientConfig config) {
		Debug.checkNull("config", config);

		final Collection<HttpURL> urls = config.listServers();
		this.iidStorage = config.getInstallationIDStorageFolder();
		Debug.checkNull("InstallationIDStorageFolder", this.iidStorage);
		try {
			this.iidStorage.checkExists();
			this.iidStorage.checkIsFolder();
		} catch (final Throwable e) {
			this.iidStorage = null;
			Err.reportError(e);
		}
		Debug.checkTrue("no analytics servers provided", urls.size() > 0);
		for (final HttpURL url : urls) {
			final ServerHandler handler = new ServerHandler(url);
			this.servers.add(handler);
		}
	}

	@Override
	synchronized public InstallationID getInstallationID () {
		if (this.iid != null) {
			return this.iid;
		}
		this.readFromStorage();
		if (this.iid != null) {
			return this.iid;
		}
		final SystemInfo systemInfo = Sys.getSystemInfo();
		systemInfo.putValue(REPORTER_PROTOCOL.CACHE_FOLDER_OK, this.iidStorage != null);
		this.iid = this.registerInstallation(systemInfo);
		if (this.iid == null) {
			return null;
		}

		this.iidSaved = this.saveIID();

		return this.iid;
	}

	private boolean saveIID () {
		if (this.iidStorage == null) {
			return false;
		}
		final File iidFile = this.iidStorage.child(INSTALLATION_ID_FILE_NAME);
		try {
			iidFile.writeString(this.iid.token);
			return true;
		} catch (final IOException e) {
			e.printStackTrace();
			Err.reportError(e);
			return false;
		}
	}

	@Override
	public boolean deleteInstallationID () {
		this.iid = null;
		if (this.iidStorage == null) {
			return false;
		}
		final File iidFile = this.iidStorage.child(INSTALLATION_ID_FILE_NAME);
		try {
			if (!iidFile.exists()) {
				return false;
			}
		} catch (final IOException e) {
			e.printStackTrace();
			Err.reportError(e);
			return false;
		}
		try {
			if (!iidFile.isFile()) {
				return false;
			}
		} catch (final IOException e) {
			e.printStackTrace();
			Err.reportError(e);
			return false;
		}

		try {
			iidFile.delete();
		} catch (final IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private InstallationID readFromStorage () {
		if (this.iidStorage == null) {
			return null;
		}
		final File iidFile = this.iidStorage.child(INSTALLATION_ID_FILE_NAME);
		try {
			if (!iidFile.exists()) {
				return null;
			}
		} catch (final IOException e) {
			e.printStackTrace();
			Err.reportError(e);
			return null;
		}
		try {
			if (!iidFile.isFile()) {
				return null;
			}
		} catch (final IOException e) {
			e.printStackTrace();
			Err.reportError(e);
			return null;
		}

		String token;
		try {
			token = iidFile.readToString();
		} catch (final IOException e) {
			e.printStackTrace();
			Err.reportError(e);
			return null;
		}
		if (token == null) {
			return null;
		}

		if ("".equals(token)) {
			return null;
		}

		this.iid = new InstallationID();
		this.iid.token = token;
		return this.iid;
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
		final InstallationID reg = new InstallationID();
		reg.token = token;
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
// response.print();

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

}
