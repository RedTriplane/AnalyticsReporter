
package com.jfixby.redreporter.server.core;

import com.jfixby.cmns.api.assets.ID;
import com.jfixby.cmns.api.collections.Map;
import com.jfixby.redreporter.api.ServerStatus;
import com.jfixby.redreporter.server.api.ReportStoreArguments;

public interface ServerCore {

	ServerStatus getStatus ();

	String newToken (ID prefix);

	String registerInstallation (String token);

	boolean updateSystemInfo (String token, Map<String, String> values);

	ReportStoreArguments newReportStoreArguments ();

	Long findInstallation (String token);

	boolean storeReport (ReportStoreArguments store_args);

}
