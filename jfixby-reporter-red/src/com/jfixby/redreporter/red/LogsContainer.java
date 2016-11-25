
package com.jfixby.redreporter.red;

import java.io.PrintStream;

import com.jfixby.cmns.api.log.MESSAGE_MARKER;

public class LogsContainer {

	public void pushLine (final MESSAGE_MARKER marker, final Object string) {
		PrintStream stream = System.out;
		if (marker == MESSAGE_MARKER.NORMAL) {
			stream = System.out;
		}
		if (marker == MESSAGE_MARKER.ERR) {
			stream = System.err;
		}
		stream.println(string);
	}

	public void append (final MESSAGE_MARKER marker, final Object string) {
		PrintStream stream = System.out;
		if (marker == MESSAGE_MARKER.NORMAL) {
			stream = System.out;
		}
		if (marker == MESSAGE_MARKER.ERR) {
			stream = System.err;
		}
		stream.print(string);
	}

}
