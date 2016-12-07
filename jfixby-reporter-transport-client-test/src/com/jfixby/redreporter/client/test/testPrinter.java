
package com.jfixby.redreporter.client.test;

import com.jfixby.cmns.api.desktop.DesktopSetup;
import com.jfixby.cmns.api.err.Err;
import com.jfixby.cmns.api.log.L;

public class testPrinter {

	public static final void main (final String[] arg) {
		DesktopSetup.deploy();
		try {
			call(0);
		} catch (final Throwable e) {
			L.d("--------------------------------------------------------");
			final String str = L.component().stackTraceToString(e);
			L.d(str);

		}

	}

	private static void call (final int i) throws Exception {
		try {
			if (i == 3) {
				Err.throwNotImplementedYet();
			}
			call(i + 1);
		} catch (final Throwable e) {
			throw new Exception("+" + i, e);
		}
	}

}
