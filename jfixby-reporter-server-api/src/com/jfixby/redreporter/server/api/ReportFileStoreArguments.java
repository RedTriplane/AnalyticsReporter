
package com.jfixby.redreporter.server.api;

public interface ReportFileStoreArguments {

	void setReceivedTimeStamp (Long receivedTimestamp);

	void setSentTimestamp (String sentTimestamp);

	void setWrittenTimestamp (String writtenTimestamp);

	void setVersionString (String versionString);

	void setInstallID (Long installID);

	void setFileID (String fileID);

	void setSessionID (String sessionID);

	void setReportData (byte[] resializedBody);

	Long getReceivedTimeStamp ();

	String getSentTimestamp ();

	Long getInstallID ();

	String getWrittenTimestamp ();

	String getVersionString ();

	byte[] getReportData ();

	String getFileID ();

	String getSessionID ();

}
