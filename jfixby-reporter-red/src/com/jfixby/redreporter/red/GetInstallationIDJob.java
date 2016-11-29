
package com.jfixby.redreporter.red;

import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.sys.Sys;
import com.jfixby.cmns.api.sys.SystemInfo;
import com.jfixby.cmns.api.taskman.Job;
import com.jfixby.redreporter.api.transport.REPORTER_PROTOCOL;

public class GetInstallationIDJob implements Job {

	private final RedReporter redReporter;
	private final boolean done = false;
	private SystemInfo systemInfo;

	public GetInstallationIDJob (final RedReporter redReporter) {
		this.redReporter = redReporter;
	}

	@Override
	public void doStart () throws Throwable {
		this.redReporter.resetSleep();
		this.systemInfo = Sys.getSystemInfo();
		this.systemInfo.putValue(REPORTER_PROTOCOL.CACHE_FOLDER_OK, this.redReporter.cacheFolderState);
	}

	@Override
	public void doPush () throws Throwable {
// try {
// final InstallationID id = ReporterTransport.registerInstallation(this.systemInfo);
// if (id != null) {
// this.redReporter.setupInstallationID(id);
// this.redReporter.resetSleep();
// this.done = true;
// return;
// }
// } catch (final Throwable e) {
// L.e(e);
// }
		this.redReporter.speep(RedReporter.m10);
	}

	@Override
	public boolean isDone () {
		if (this.isDone()) {
			L.d("job done", this);
		}
		return this.done;
	}

}
