
package com.jfixby.redreporter.client.http;

import com.jfixby.cmns.api.taskman.Job;

public class PushServiceJob implements Job {

	public PushServiceJob (final BackgroundService backgroundService) {
	}

	@Override
	public void doStart () throws Throwable {
	}

	@Override
	public void doPush () throws Throwable {
// {
// // --------------------------------
// if (GCFisher.isGarbageModeFlag(GARBAGE_MODE.GARBAGE_SAVING)) {
// Sys.sleep(this.period_long);
// return;
// }
// if (this.queue.size() == 0) {
// Sys.sleep(this.period);
// return;
// }
// final RedReport report = this.queue.peek();
// final boolean result = this.transport.sendReport(report, this.onTryToSendReport(report));
// if (result == OK) {
// report.dispose();
// this.queue.remove();
// return;
// }
//
//// this.queue.ensureCached(this.getLogFileExtention());
//// --------------------------------
// }
	}

	@Override
	public boolean isDone () {
		return true;
	}

}
