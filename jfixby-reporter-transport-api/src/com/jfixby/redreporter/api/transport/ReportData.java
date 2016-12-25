
package com.jfixby.redreporter.api.transport;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ReportData implements java.io.Serializable {
	private static final long serialVersionUID = -3553548771097426892L;
	public static final String REPORT_VERSION = "red-reporter-1.0.0-20161206";

	public LinkedHashMap<String, ArrayList<Stat>> strings = new LinkedHashMap<String, ArrayList<Stat>>();
	public LinkedHashMap<String, ArrayList<Stat>> exceptions = new LinkedHashMap<String, ArrayList<Stat>>();

	public static class Stat implements java.io.Serializable {
		private static final long serialVersionUID = -1664975875985088075L;
		public String value;
		public long timestamp;
	}
}
