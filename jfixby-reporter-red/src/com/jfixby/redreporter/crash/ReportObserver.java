
package com.jfixby.redreporter.crash;

import com.jfixby.redreporter.api.analytics.OnReportProcessedListener;
import com.jfixby.scarabei.api.lambda.VoidAction;

public class ReportObserver implements OnReportProcessedListener {

	private final VoidAction action;

	public ReportObserver (final VoidAction action) {
		super();
		this.action = action;
	}

	@Override
	public void onReportSent () {
		if (this.action != null) {
			this.action.act();
		}
	}

	@Override
	public void onReportFailedToSend () {
		if (this.action != null) {
			this.action.act();
		}
	}

}
