
package com.jfixby.redreporter.red;

import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.taskman.Job;
import com.jfixby.red.filesystem.virtual.InMemoryFileSystem;
import com.jfixby.redreporter.api.err.ErrorReporter;

public class TryToDeployCacheJob implements Job {

	private final RedReporter redReporter;
	private final File homeFolder;
	private boolean done = false;

	public TryToDeployCacheJob (final RedReporter redReporter) {
		this.redReporter = redReporter;
		this.homeFolder = redReporter.getHomeFolder();
	}

	@Override
	public void doStart () throws Throwable {
		try {
			final File logCache = this.homeFolder.child(".rr-log-cache");
			final boolean success = logCache.makeFolder();
			if (success) {
				this.redReporter.setupCacheFolder(logCache, RedReporter.CACHE_FOLDER_SUCCESSFULLY_CREATED);
				this.done = true;
				return;
			}
		} catch (final Throwable e) {
			ErrorReporter.reportProblem(e);
		}

		final InMemoryFileSystem fs = new InMemoryFileSystem();
		this.redReporter.setupCacheFolder(fs.ROOT(), RedReporter.CACHE_FOLDER_IS_TEMPORARY);
		this.done = true;

	}

	@Override
	public void doPush () throws Throwable {
	}

	@Override
	public boolean isDone () {
		if (this.isDone()) {
			L.d("job done", this);
		}
		return this.done;
	}

}
