
package com.jfixby.redreporter.server.api;

import com.jfixby.cmns.api.assets.ID;
import com.jfixby.cmns.api.collections.Map;
import com.jfixby.redreporter.api.ServerStatus;

public interface ReporterServerComponent {

	String newToken (ID prefix);

	String registerInstallation (String token);

	boolean updateSystemInfo (final String token, Map<String, String> values);

	ServerStatus getStatus ();

	ReportStoreArguments newReportStoreArguments ();

	Long findInstallation (String token);

	boolean storeReport (ReportStoreArguments store_args);
}
