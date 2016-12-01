
package com.jfixby.redreporter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import com.jfixby.cmns.api.file.File;

public class ReportsQueue {

	private final LinkedList<ReportHandler> all = new LinkedList<ReportHandler>();
	private final LinkedHashSet<ReportHandler> nonCached = new LinkedHashSet<ReportHandler>();
	private final HashSet<ReportHandler> toRemove = new HashSet<ReportHandler>();

	public synchronized int size () {
		return this.all.size();
	}

	public synchronized ReportHandler peek () {
		return this.all.peek();
	}

	public synchronized ReportHandler remove () {
		final ReportHandler first = this.all.removeFirst();
		return first;
	}

	public synchronized void ensureCached (final File cacheFolder, final String extention) {
		if (this.nonCached.size() == 0) {
			return;
		}
		this.toRemove.clear();
		for (final Iterator<ReportHandler> i = this.nonCached.iterator(); i.hasNext();) {
			final ReportHandler e = i.next();
			final boolean success = e.cache(cacheFolder, extention);
			if (success) {
				this.toRemove.add(e);
			}
		}
		this.nonCached.removeAll(this.toRemove);
		this.toRemove.clear();

	}

	public synchronized void add (final ReportHandler report) {
		this.all.add(report);
		this.nonCached.add(report);
	}
}
