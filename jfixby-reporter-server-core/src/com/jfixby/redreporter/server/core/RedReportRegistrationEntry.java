
package com.jfixby.redreporter.server.core;

import com.jfixby.redreporter.server.api.ReportRegistrationEntry;

public class RedReportRegistrationEntry implements ReportRegistrationEntry {

	private String name;
	private String value;
	private String timeStamp;

	public void setName (final String name) {
		this.name = name;
	}

	public void setValue (final String value) {
		this.value = value;
	}

	public void setTimestamp (final String timeStamp) {
		this.timeStamp = timeStamp;
	}

	@Override
	public String getName () {
		return this.name;
	}

	@Override
	public String getTimeStamp () {
		return this.timeStamp;
	}

	@Override
	public String getValue () {
		return this.value;
	}

}
