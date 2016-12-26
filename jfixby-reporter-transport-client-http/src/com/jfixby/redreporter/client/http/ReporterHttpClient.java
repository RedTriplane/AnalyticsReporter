
package com.jfixby.redreporter.client.http;

import com.jfixby.redreporter.api.analytics.ReportWriter;
import com.jfixby.redreporter.api.report.Report;
import com.jfixby.redreporter.api.transport.REPORTER_PROTOCOL;
import com.jfixby.redreporter.api.transport.ReporterTransport;
import com.jfixby.redreporter.api.transport.ServersCheck;
import com.jfixby.redreporter.api.transport.ServersCheckParams;
import com.jfixby.scarabei.api.collections.Collection;
import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.Mapping;
import com.jfixby.scarabei.api.collections.Pool;
import com.jfixby.scarabei.api.collections.PoolElementsSpawner;
import com.jfixby.scarabei.api.debug.Debug;
import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.java.ByteArray;
import com.jfixby.scarabei.api.net.http.HttpURL;
import com.jfixby.scarabei.api.net.message.Message;
import com.jfixby.scarabei.api.sys.Sys;
import com.jfixby.scarabei.api.sys.SystemInfo;
import com.jfixby.scarabei.api.taskman.TASK_TYPE;

public class ReporterHttpClient implements ReporterTransport, PoolElementsSpawner<RedReportWriter> {

	final ServerHandlers servers = new ServerHandlers();
	private final InstallationIDStorage iidStorage;
	private final Session session = new Session();

	final ReportsQueue queue;

	public ReporterHttpClient (final ReporterHttpClientConfig config) {
		Debug.checkNull("config", config);

		final File cacheFolder = config.getCacheFolder();
		final TASK_TYPE taskType = config.getTaskType();
		this.queue = new ReportsQueue(this, cacheFolder, taskType);
		this.queue.loadFromCacheAndPush();
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

	boolean registerInstallation () {

		final SystemInfo systemInfo = Sys.getSystemInfo();
		final Mapping<String, String> params = systemInfo.listParameters();

		final Message request = new Message(REPORTER_PROTOCOL.REGISTER_INSTALLATION);
		request.values.putAll(params.toJavaMap());

		final Message response = this.exchange(this.servers, request);
		if (response == null) {
			return false;
		}

		final String token = response.values.get(REPORTER_PROTOCOL.INSTALLATION_TOKEN);
		if (token == null) {
			return false;
		}
		this.iidStorage.setID(token);
		return true;
	}

	private Message exchange (final ServerHandlers servers, final Message request) {
		final ServerPing serverPing = this.getBestServer();
		if (serverPing == null) {
			return null;
		}
		final ServerHandler server = serverPing.server;

// L.d("exchange", serverPing);
		final Message response = server.exchange(request);
		if (response == null) {
			return null;
		}
// this.checkToken(response);
		return response;
	}

	RedServersCheckParams bestServerSearchParams = new RedServersCheckParams();

	private ServerPing getBestServer () {
		this.bestServerSearchParams.setTimeOut(2000);
		return this.servers.getBest(this.bestServerSearchParams);
	}

	public boolean tryToSend (final Report report) {
		if (this.iidStorage.getID() == null) {
			final boolean reg = this.registerInstallation();
			if (!reg) {
				return false;
			}
		}
		final Mapping<String, String> params = report.listParameters();
		final Message message = new Message(REPORTER_PROTOCOL.REPORT);
		this.packToMessage(report, params, message);
		final Message response = this.exchange(this.servers, message);
		if (response == null) {
			return false;
		}
		if (REPORTER_PROTOCOL.INVALID_TOKEN.equals(response.header)) {
			this.iidStorage.deleteID();
			return false;
		}
		if (!REPORTER_PROTOCOL.REPORT_RECEIVED_OK.equals(response.header)) {
			response.print();
			return false;
		}

		return true;
	}

	private void packToMessage (final Report report, final Mapping<String, String> params, final Message message) {
		final ByteArray data = report.getPackedData();
		message.attachments.put(REPORTER_PROTOCOL.REPORT, data.toArray());
		if (params != null) {
			message.values.putAll(params.toJavaMap());
		}
		message.values.put(REPORTER_PROTOCOL.REPORT_SENT, "" + Sys.SystemTime().currentTimeMillis());
		message.values.put(REPORTER_PROTOCOL.SESSION_ID, this.session.getID());
		message.values.put(REPORTER_PROTOCOL.INSTALLATION_TOKEN, this.iidStorage.getID());
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

	private final PoolElementsSpawner<RedReportWriter> reportWriterSpawner = this;
	final Pool<RedReportWriter> writersPool = Collections.newPool(this.reportWriterSpawner);

	@Override
	public ReportWriter newReportWriter () {
		return this.writersPool.obtain().start();
	}

	@Override
	public RedReportWriter spawnNewInstance () {
		return new RedReportWriter(this.writersPool, this);
	}

	public String getInstallationID () {
		return this.iidStorage.getID();
	}

}
