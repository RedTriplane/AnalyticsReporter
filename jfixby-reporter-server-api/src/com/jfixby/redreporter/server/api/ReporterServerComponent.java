
package com.jfixby.redreporter.server.api;

import com.jfixby.cmns.api.assets.ID;
import com.jfixby.cmns.api.collections.Map;
import com.jfixby.redreporter.api.ServerStatus;

public interface ReporterServerComponent {

	ID newToken (ID prefix);

	String registerInstallation (ID token);

	boolean updateSystemInfo (final ID token, Map<String, String> values);

	ServerStatus getStatus ();
}
