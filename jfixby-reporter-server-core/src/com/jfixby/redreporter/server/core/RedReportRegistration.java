
package com.jfixby.redreporter.server.core;

import com.jfixby.redreporter.server.api.ReportRegistration;
import com.jfixby.redreporter.server.api.ReportRegistrationEntry;
import com.jfixby.scarabei.api.collections.Collection;
import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.List;

public class RedReportRegistration implements ReportRegistration {
	private Long ReceivedTimeStamp;
	private String SentTimestamp;
	private Long InstallID;
	private String WrittenTimestamp;
	private String VersionString;
	private String sessionID;

	final List<ReportRegistrationEntry> params = Collections.newList();
	final List<ReportRegistrationEntry> exceptions = Collections.newList();

	@Override
	public void addParameter (final String name, final String value, final String timeStamp) {
		final RedReportRegistrationEntry entry = new RedReportRegistrationEntry();

		entry.setName(name);
		entry.setValue(value);
		entry.setTimestamp(timeStamp);

		this.params.add(entry);
	}

	@Override
	public void addException (final String name, final String stack, final String timeStamp) {
		final RedReportRegistrationEntry entry = new RedReportRegistrationEntry();

		entry.setName(name);
		entry.setValue(stack);
		entry.setTimestamp(timeStamp);

		this.exceptions.add(entry);
	}

	@Override
	public Collection<ReportRegistrationEntry> listParameters () {
		return this.params;
	}

	@Override
	public Collection<ReportRegistrationEntry> listExceptions () {
		return this.exceptions;
	}

	@Override
	public void setReceivedTimeStamp (final Long receivedTimestamp) {
		this.ReceivedTimeStamp = receivedTimestamp;
	}

	@Override
	public void setSentTimestamp (final String sentTimestamp) {
		this.SentTimestamp = sentTimestamp;
	}

	@Override
	public void setWrittenTimestamp (final String writtenTimestamp) {
		this.WrittenTimestamp = writtenTimestamp;
	}

	@Override
	public void setVersionString (final String versionString) {
		this.VersionString = versionString;
	}

	@Override
	public void setInstallID (final Long installID) {
		this.InstallID = installID;
	}

	@Override
	public Long getReceivedTimeStamp () {
		return this.ReceivedTimeStamp;
	}

	@Override
	public String getSentTimestamp () {
		return this.SentTimestamp;
	}

	@Override
	public Long getInstallID () {
		return this.InstallID;
	}

	@Override
	public String getWrittenTimestamp () {
		return this.WrittenTimestamp;
	}

	@Override
	public String getVersionString () {
		return this.VersionString;
	}

	@Override
	public void setSessionID (final String sessionID) {
		this.sessionID = sessionID;
	}

	@Override
	public String getSessionID () {
		return this.sessionID;
	}

}
