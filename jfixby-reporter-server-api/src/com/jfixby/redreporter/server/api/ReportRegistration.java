
package com.jfixby.redreporter.server.api;

import com.jfixby.scarabei.api.collections.Collection;

public interface ReportRegistration {

	void addParameter (String name, String value, String timeStamp);

	void addException (String name, String stack, String timeStamp);

	Collection<ReportRegistrationEntry> listParameters ();

	Collection<ReportRegistrationEntry> listExceptions ();

	void setReceivedTimeStamp (Long receivedTimestamp);

	void setSentTimestamp (String sentTimestamp);

	void setWrittenTimestamp (String writtenTimestamp);

	void setVersionString (String versionString);

	void setInstallID (Long installID);

	void setSessionID (String sessionID);

	void setReportUID (String reportUID);

	String getReportUID ();

	Long getReceivedTimeStamp ();

	String getSentTimestamp ();

	Long getInstallID ();

	String getWrittenTimestamp ();

	String getVersionString ();

	String getSessionID ();

}
