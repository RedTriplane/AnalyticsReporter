
package com.jfixby.redreporter.client.http;

import java.util.Iterator;
import java.util.Vector;

import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.Set;
import com.jfixby.cmns.api.java.Int;
import com.jfixby.cmns.api.sys.Sys;
import com.jfixby.redreporter.api.transport.ServersCheck;

public class ServerHandlers implements Iterable<ServerHandler> {

	private final Set<ServerHandler> servers = Collections.newSet();

	public void add (final ServerHandler handler) {
		this.servers.add(handler);
	}

	public ServerPing getBestServerPing (final long timeLimit) {

		final Vector<ServerPing> failed = new Vector<ServerPing>();
		final Vector<ServerPing> succeed = new Vector<ServerPing>();

		final Int totalNumberOfparticipants = new Int(this.servers.size());
		final long startTime = Sys.SystemTime().currentTimeMillis();
		final ServerRanker ranker = new ServerRanker() {

			@Override
			public void onSuccess (final ServerHandler server, final ServerPing result) {
				succeed.add(result);
				totalNumberOfparticipants.value--;
			}

			@Override
			public void onFail (final ServerHandler server, final ServerPing result) {
				failed.add(result);
				totalNumberOfparticipants.value--;
			}
		};
		for (final ServerHandler server : this.servers) {
			server.check(ranker);
		}

		long passed;
		while (succeed.size() == 0 && totalNumberOfparticipants.value > 0) {
			passed = Sys.SystemTime().currentTimeMillis() - startTime;
			if (passed > timeLimit) {
				break;
			}
			Sys.sleep(15);
		}
		if (succeed.size() == 0) {
			return null;
		}
		return succeed.get(0);
	}

	@Override
	public Iterator<ServerHandler> iterator () {
		return this.servers.iterator();
	}

	public ServersCheck checkAll () {
		final AllServersCheck check = new AllServersCheck(this.servers);
		return check;

	}
}
