
package com.jfixby.redreporter.client.http;

import java.util.Comparator;
import java.util.Iterator;

import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.Set;
import com.jfixby.cmns.api.math.IntegerMath;

public class ServerHandlers implements Iterable<ServerHandler> {

	private final Set<ServerHandler> servers = Collections.newSet();
	ServerHandler best;
	private final Comparator<ServerHandler> comparator = new Comparator<ServerHandler>() {

		@Override
		public int compare (final ServerHandler o1, final ServerHandler o2) {
			return IntegerMath.compare(o1.getPing(), o2.getPing());
		}
	};

	public void add (final ServerHandler handler) {
		this.servers.add(handler);
	}

	public void updatePings () {
		if (this.servers.size() == 0) {
			return;
		}
		for (final ServerHandler h : this.servers) {
			h.updatePing();

		}
		this.servers.sort(this.comparator);
		this.best = this.servers.getElementAt(0);
	}

	public void printPings () {
		this.servers.print("pings");
	}

	public ServerHandler getBestServer () {
		if (this.best != null) {
			return this.best;
		}
		this.updatePings();
		return this.best;
	}

	@Override
	public Iterator<ServerHandler> iterator () {
		return this.servers.iterator();
	}

}
