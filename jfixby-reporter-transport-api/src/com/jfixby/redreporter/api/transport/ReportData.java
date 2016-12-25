
package com.jfixby.redreporter.api.transport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ReportData implements java.io.Serializable {

// public static final String PARAMETER_NAME = "PARAMETER_NAME";
	public static final String PARAMETER_VALUE = "PARAMETER_VALUE";
	public static final String PARAMETER_TIMESTAMP = "PARAMETER_TIMESTAMP";

	private static final long serialVersionUID = -3553548771097426892L;
	public static final String REPORT_VERSION = "red-reporter-1.0.0-20161206";

	public LinkedHashMap<String, ArrayList<HashMap<String, String>>> strings = new LinkedHashMap<String, ArrayList<HashMap<String, String>>>();
	public LinkedHashMap<String, ArrayList<HashMap<String, String>>> exceptions = new LinkedHashMap<String, ArrayList<HashMap<String, String>>>();

}
