
package com.jfixby.redreporter;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.jfixby.cmns.api.log.L;

public class ReportData implements java.io.Serializable {

	private static final long serialVersionUID = -5531576045733934241L;
	public String local_id;
	public long timestamp;

	public LinkedHashMap<String, String> values = new LinkedHashMap<String, String>();
	public LinkedHashMap<String, ArrayList<java.io.Serializable>> attachments = new LinkedHashMap<String, ArrayList<java.io.Serializable>>();

	public void print () {
		L.d("---Report[" + this.timestamp + ":" + this.local_id + "]------------------------");
		if (this.values != null && this.values.size() > 0) {
			L.d("     values", this.values);
		}
		if (this.attachments != null && this.attachments.size() > 0) {
			L.d("attachments", this.attachments);
		}
	}

}
