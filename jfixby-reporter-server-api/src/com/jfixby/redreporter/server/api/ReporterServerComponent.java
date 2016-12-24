
package com.jfixby.redreporter.server.api;

import com.jfixby.scarabei.api.assets.ID;
import com.jfixby.scarabei.api.collections.Map;

public interface ReporterServerComponent {

	ServerCoreConfig newReporterServerConfig ();

	void deployCore (ServerCoreConfig coreConfig);

	Long findInstallationID (String token);

	ReportFileStoreArguments newReportFileStoreArguments ();

	boolean storeReportFile (ReportFileStoreArguments store_args);

	String registerInstallation (String token);

	String newToken (ID requestID);

	boolean updateSystemInfo (String token_string, Map<String, String> params);

	DB_STATE getDBState ();

	STORAGE_STATE getSorageState ();

	void reportDeserializationtionProblem (Throwable e);

	boolean registerReport (ReportRegistration reg);

	ReportRegistration newReportRegistration ();

}
