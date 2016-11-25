
package com.jfixby.redreporter.red;

import com.jfixby.cmns.api.err.Err;
import com.jfixby.cmns.api.taskman.Job;

public class GetTokenJob implements Job {

	@Override
	public void doStart () throws Throwable {
		Err.reportNotImplementedYet();
	}

	@Override
	public void doPush () throws Throwable {
	}

	@Override
	public boolean isDone () {
		return false;
	}

}
