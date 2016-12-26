
package com.jfixby.redreporter.server.core;

import com.jfixby.redreporter.server.api.ReportFileStoreArguments;

public class RedReportFileStoreArguments implements ReportFileStoreArguments {

	private String fileID;
	private byte[] data;
	private Long ReceivedTimeStamp;
	private String SentTimestamp;
	private Long InstallID;
	private String WrittenTimestamp;
	private String VersionString;
	private String sessionID;
	private String subject;
	private String author;
	private String reportID;

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
	public void setReportData (final byte[] resializedBody) {
		this.data = resializedBody;
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
	public byte[] getReportData () {
		return this.data;
	}

	@Override
	public void setFileID (final String fileID) {
		this.fileID = fileID;
	}

	@Override
	public String getFileID () {
		return this.fileID;
	}

	@Override
	public void setSessionID (final String sessionID) {
		this.sessionID = sessionID;
	}

	@Override
	public String getSessionID () {
		return this.sessionID;
	}

	@Override
	public void setReportUID (final String reportID) {
		this.reportID = reportID;
	}

	@Override
	public void setAuthor (final String author) {
		this.author = author;
	}

	@Override
	public void setSubject (final String subject) {
		this.subject = subject;
	}

	@Override
	public String getAuthor () {
		return this.author;
	}

	@Override
	public String getSubject () {
		return this.subject;
	}

	@Override
	public String getReportID () {
		return this.reportID;
	}

}
