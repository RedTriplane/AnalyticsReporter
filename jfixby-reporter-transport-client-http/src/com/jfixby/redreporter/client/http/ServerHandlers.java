
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
			final long ping1 = o1.getPing();
			final long ping2 = o2.getPing();
			return IntegerMath.compare(ping1, ping2);
		}
	};

	public void add (final ServerHandler handler) {
		this.servers.add(handler);
	}

	public void check () {
		if (this.servers.size() == 0) {
			return;
		}
		for (final ServerHandler h : this.servers) {
			h.check();
		}
		this.servers.sort(this.comparator);
		this.best = this.servers.getElementAt(0);
	}

	public void printStatuses () {
		this.servers.print("");
	}

	public ServerHandler getBestServer () {
		if (this.best != null) {
			return this.best;
		}
		this.check();
		return this.best;
	}

	@Override
	public Iterator<ServerHandler> iterator () {
		return this.servers.iterator();
	}

}
