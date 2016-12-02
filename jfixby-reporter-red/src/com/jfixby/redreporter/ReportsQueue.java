
package com.jfixby.redreporter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import com.jfixby.cmns.api.file.File;

public class ReportsQueue {

	private final LinkedList<RedReport> all = new LinkedList<RedReport>();
	private final LinkedHashSet<RedReport> nonCached = new LinkedHashSet<RedReport>();
	private final HashSet<RedReport> toRemove = new HashSet<RedReport>();

	public synchronized int size () {
		return this.all.size();
	}

	public synchronized RedReport peek () {
		return this.all.peek();
	}

	public synchronized RedReport remove () {
		final RedReport first = this.all.removeFirst();
		return first;
	}

	public synchronized void ensureCached (final File cacheFolder, final String extention) {
		if (this.nonCached.size() == 0) {
			return;
		}
		this.toRemove.clear();
		for (final Iterator<RedReport> i = this.nonCached.iterator(); i.hasNext();) {
			final RedReport e = i.next();
			final boolean success = e.cache(cacheFolder, extention);
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
