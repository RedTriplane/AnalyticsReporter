
package com.jfixby.redreporter.client.test;

import com.jfixby.cmns.api.log.L;
import com.jfixby.red.desktop.DesktopSetup;
import com.jfixby.redreporter.red.RedReportMessage;

public class testPrinter {

	public static final void main (final String[] arg) {
		DesktopSetup.deploy();
		try {
			call(0);
		} catch (final Throwable e) {
			L.d("--------------------------------------------------------");
			final String str = RedReportMessage.getStackTrace(e);
			L.d(str);

			L.d("--------------------------------------------------------");
			final String trace = RedReportMessage.throwableToString(e);
			L.d(trace);
			L.d("--------------------------------------------------------");
		}

	}

	private static void call (final int i) throws Exception {
		try {
			if (i == 3) {
				throw new Error();
			}
			call(i + 1);
		} catch (final Throwable e) {
			throw new Exception("+" + i, e);
		}
	}

}
