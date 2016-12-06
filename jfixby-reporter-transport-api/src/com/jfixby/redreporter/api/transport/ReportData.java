
package com.jfixby.redreporter.api.transport;

import java.util.LinkedHashMap;

public class ReportData implements java.io.Serializable {
	private static final long serialVersionUID = -3553548771097426892L;
	public static final String REPORT_VERSION = "red-reporter-1.0.0-20161206";

	public LinkedHashMap<String, String> strings = new LinkedHashMap<String, String>();

}
