
package com.jfixby.redreporter.server.api;

import com.jfixby.cmns.api.assets.ID;
import com.jfixby.cmns.api.collections.Map;
import com.jfixby.redreporter.api.InstallationID;

public interface ReporterServerComponent {

	ID newToken (ID prefix);

	InstallationID registerInstallation (ID token);

	boolean updateSystemInfo (final ID token, Map<String, String> values);

	ServerStatus getState ();
}
