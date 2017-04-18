
package com.jfixby.redreporter.client.http;

import java.util.ArrayList;
import java.util.Iterator;

import com.jfixby.redreporter.api.transport.ServersCheck;
import com.jfixby.redreporter.api.transport.ServersCheckParams;
import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.Set;
import com.jfixby.scarabei.api.java.Int;
import com.jfixby.scarabei.api.sys.Sys;

public class ServerHandlers implements Iterable<ServerHandler> {

	private final Set<ServerHandler> servers = Collections.newSet();

	public void add (final ServerHandler handler) {
		this.servers.add(handler);
	}

	final RequestArgs args = new RequestArgs();

	public ServerPing getBestServerPing (final long timeLimit) {

		final ArrayList<ServerPing> failed = new ArrayList<ServerPing>();
		final ArrayList<ServerPing> succeed = new ArrayList<ServerPing>();

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
		this.args.timeout = (timeLimit);
		for (final ServerHandler server : this.servers) {
			server.check(ranker, this.args);
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

	public ServersCheck checkAll (final ServersCheckParams params) {
		final AllServersCheck check = new AllServersCheck(this.servers, params);
		return check;

	}

	public ServerPing getBest (final RedServersCheckParams bestServerSearchParams) {
		final GetBestServer check = new GetBestServer(this.servers, bestServerSearchParams);
		return check.getBest();
	}
}
