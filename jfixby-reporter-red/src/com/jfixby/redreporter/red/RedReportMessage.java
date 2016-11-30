
package com.jfixby.redreporter.red;

import com.jfixby.cmns.api.log.L;

public class RedReportMessage {

	public final String tag;
	public final String message;
	public Throwable e;

	public RedReportMessage (final String tag, final String message) {
		this.tag = tag;
		this.message = message;
	}

	public RedReportMessage (final String tag, final Throwable e) {
		this.tag = tag;
		this.e = e;
		this.message = L.component().stackTraceToString(e);
	}

}
