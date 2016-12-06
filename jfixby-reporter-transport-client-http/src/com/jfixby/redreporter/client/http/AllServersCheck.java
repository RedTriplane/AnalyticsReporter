
package com.jfixby.redreporter.client.http;

import com.jfixby.cmns.api.collections.Collection;
import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.List;
import com.jfixby.cmns.api.java.Int;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.sys.Sys;
import com.jfixby.redreporter.api.transport.ServersCheck;
import com.jfixby.redreporter.api.transport.ServersCheckParams;

public class AllServersCheck implements ServersCheck, ServerRanker {

	private final Collection<ServerHandler> servers;
	private final Int totalNumberOfparticipants;
	private final long timeout;
	final RequestArgs args = new RequestArgs();
	private final long startTime;
	private final long finishTime;

	public AllServersCheck (final Collection<ServerHandler> servers, final ServersCheckParams params) {
		this.servers = servers;
		this.totalNumberOfparticipants = new Int(this.servers.size());
		this.timeout = params.getTimeOut();

		this.servers.print("AllServersCheck");

		this.startTime = Sys.SystemTime().currentTimeMillis();
		this.args.timeout = this.timeout;
		for (final ServerHandler server : this.servers) {
			server.check(this, this.args);
		}

		this.waitForRankingResults();
		this.finishTime = Sys.SystemTime().currentTimeMillis();
	}

	private synchronized void waitForRankingResults () {
		while (this.totalNumberOfparticipants.value > 0) {
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

	@Override
	public boolean isComplete () {
		return this.totalNumberOfparticipants.value == 0;
	}

	@Override
	public void print (final String tag) {
		L.d("---ServersCheck[" + tag + "]------------------------------");
		this.servers//
			.print(" servers");
		if (this.totalNumberOfparticipants.value > 0) {
			L.d("checks running", this.totalNumberOfparticipants.value);
		} else {
			L.d("       done in", (this.finishTime - this.startTime) + " ms");
			if (this.succeed.size() > 0) {
				this.succeed//
					.print("succeed");
			}
			if (this.failed.size() > 0) {
				this.failed//
					.print(" failed");
			}
		}

	}

}
