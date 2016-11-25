
package com.jfixby.redreporter.red;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class RedReportMessage {
	public final static String SEPARATOR = System.getProperty("line.separator");

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
		this.message = getStackTrace(e);
	}

	public static String getStackTrace (final Throwable e) {
		// final StackTraceElement[] arr = e.getStackTrace();
		final StringBuilder report = new StringBuilder();
		report.append(throwableToString(e)).append(SEPARATOR);
		final Throwable cause = e.getCause();
		if (cause != null) {
			report.append(throwableToString(cause));
		}
		return report.toString();
	}

	public static String throwableToString (final Throwable aThrowable) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return result.toString();
	}

}
