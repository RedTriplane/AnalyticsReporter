
package com.jfixby.redreporter.client.http;

import java.util.Vector;

import com.jfixby.cmns.api.collections.Collection;
import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.java.Int;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.sys.Sys;
import com.jfixby.redreporter.api.transport.ServersCheck;

public class AllServersCheck implements ServersCheck {

	private final Collection<ServerHandler> servers;
	private final Int totalNumberOfparticipants;

	public AllServersCheck (final Collection<ServerHandler> servers) {
		this.servers = servers;
		this.totalNumberOfparticipants = new Int(this.servers.size());

		this.servers.print("cheking servers");

		final long startTime = Sys.SystemTime().currentTimeMillis();

		for (final ServerHandler server : this.servers) {
			server.check(this.ranker);
		}

		while (this.totalNumberOfparticipants.value > 0) {
// Sys.yeld();
			Sys.sleep(1);
		}
		L.d("cheking servers done in", (System.currentTimeMillis() - startTime) + " ms");
		if (this.totalNumberOfparticipants.value > 0) {
			L.d("still running", this.totalNumberOfparticipants.value);
		}
		if (this.succeed.size() > 0) {
			Collections.newList(this.succeed).print("succeed");
		}
		if (this.failed.size() > 0) {
			Collections.newList(this.failed).print(" failed");
		}
	}

	final Vector<ServerPing> failed = new Vector<ServerPing>();
	final Vector<ServerPing> succeed = new Vector<ServerPing>();
	final ServerRanker ranker = new ServerRanker() {

		@Override
		public void onSuccess (final ServerHandler server, final ServerPing result) {
			AllServersCheck.this.succeed.add(result);
			AllServersCheck.this.totalNumberOfparticipants.value--;
		}

		@Override
		public void onFail (final ServerHandler server, final ServerPing result) {
			AllServersCheck.this.failed.add(result);
			AllServersCheck.this.totalNumberOfparticipants.value--;
		}
	};

	@Override
	public boolean isComplete () {
		return this.totalNumberOfparticipants.value == 0;
	}

}
