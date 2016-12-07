
package com.jfixby.redreporter.server.api;

import com.jfixby.cmns.api.assets.ID;
import com.jfixby.cmns.api.collections.Map;
import com.jfixby.redreporter.api.ServerStatus;

public interface ReporterServerComponent {

	ServerCoreConfig newReporterServerConfig ();

	void deployCore (ServerCoreConfig coreConfig);

	ServerStatus getStatus ();

	Long findInstallationID (String token);

	ReportStoreArguments newReportStoreArguments ();

	boolean storeReport (ReportStoreArguments store_args);

	String registerInstallation (String token);

	String newToken (ID requestID);

	boolean updateSystemInfo (String token_string, Map<String, String> params);

}
