
package com.jfixby.redreporter.client.http;

import com.jfixby.redreporter.api.transport.ServersCheckParams;
import com.jfixby.scarabei.api.collections.Collection;
import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.List;
import com.jfixby.scarabei.api.java.Int;
import com.jfixby.scarabei.api.sys.Sys;

public class GetBestServer implements BestServerCheck, ServerRanker {

	private final Collection<ServerHandler> servers;
	private final Int totalNumberOfparticipants;
	private final long timeout;
	final RequestArgs args = new RequestArgs();
	private final long startTime;
	private final long finishTime;

	public GetBestServer (final Collection<ServerHandler> servers, final ServersCheckParams params) {
		this.servers = servers;
		this.totalNumberOfparticipants = new Int(this.servers.size());
		this.timeout = params.getTimeOut();

// this.servers.print("GetBestServer");

		this.startTime = Sys.SystemTime().currentTimeMillis();
		this.args.timeout = this.timeout;
		for (final ServerHandler server : this.servers) {
			server.check(this, this.args);
		}

		this.waitForRankingResults();
		this.finishTime = Sys.SystemTime().currentTimeMillis();
// L.d("done in", (this.finishTime - this.startTime) + " ms");
	}

	private synchronized void waitForRankingResults () {
		while (!this.isComplete()) {
			Sys.wait(this);
		}
	}

	final List<ServerPing> failed = Collections.newList();
	final List<ServerPing> succeed = Collections.newList();

	@Override
	public synchronized void onSuccess (final ServerHandler server, final ServerPing result) {
		this.succeed.add(result);
		this.totalNumberOfparticipants.value--;
		this.notify();
	}

	@Override
	public synchronized void onFail (final ServerHandler server, final ServerPing result) {
		this.failed.add(result);
		this.totalNumberOfparticipants.value--;
		this.notify();
	}

	boolean isComplete () {
		return (this.succeed.size() > 0 || this.totalNumberOfparticipants.value == 0);
	}

	public ServerPing getBest () {
		if (this.succeed.size() == 0) {
			if (this.totalNumberOfparticipants.value == 0) {
// this.failed.print("failed servers");
			}
			return null;
		}
		return this.succeed.getElementAt(0);
	}

}
