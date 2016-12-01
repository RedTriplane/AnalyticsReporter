
package com.jfixby.redreporter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import com.jfixby.redreporter.api.analytics.Report;

public class ReportsQueue {

	final LinkedList<RedReport> all = new LinkedList<RedReport>();
	final LinkedHashSet<RedReport> nonCached = new LinkedHashSet<RedReport>();
	final HashSet<RedReport> toRemove = new HashSet<RedReport>();

	public synchronized int size () {
		return this.all.size();
	}

	public synchronized Report peek () {
		return this.all.peek();
	}

	public synchronized Report remove () {
		final RedReport first = this.all.removeFirst();
		return first;
	}

	public synchronized void ensureCached () {
		if (this.nonCached.size() == 0) {
			return;
		}
		this.toRemove.clear();
		for (final Iterator<RedReport> i = this.nonCached.iterator(); i.hasNext();) {
			final RedReport e = i.next();
			final boolean success = e.cache();
			if (success) {
				this.toRemove.add(e);
			}
		}
		this.nonCached.removeAll(this.toRemove);
		this.toRemove.clear();

	}

	public synchronized void add (final RedReport report) {
		this.all.add(report);
		this.nonCached.add(report);
	}
}
