
package com.jfixby.redreporter.server.core;

import com.jfixby.redreporter.server.api.DB_STATE;
import com.jfixby.redreporter.server.api.ReportStoreArguments;
import com.jfixby.redreporter.server.api.STORAGE_STATE;
import com.jfixby.scarabei.api.assets.ID;
import com.jfixby.scarabei.api.collections.Map;

public interface ServerCore {

	DB_STATE getDBState ();

	STORAGE_STATE getSorageState ();

	String newToken (ID prefix);

	String registerInstallation (String token);

	boolean updateSystemInfo (String token, Map<String, String> values);

	ReportStoreArguments newReportStoreArguments ();

	Long findInstallation (String token);

	boolean storeReport (ReportStoreArguments store_args);

}
