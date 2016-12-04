
package com.jfixby.redreporter.client.http;

import java.util.LinkedHashMap;

public class SrlzdReport implements java.io.Serializable {

	private static final long serialVersionUID = -5531576045733934241L;

	public LinkedHashMap<String, String> sendParameters = new LinkedHashMap<String, String>();

	public byte[] serializedReport;

}
