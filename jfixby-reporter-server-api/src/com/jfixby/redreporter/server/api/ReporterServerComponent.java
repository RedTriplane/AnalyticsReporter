
package com.jfixby.redreporter.server.api;

import java.io.IOException;
import java.util.Map;

import com.jfixby.cmns.api.assets.ID;
import com.jfixby.redreporter.api.InstallationID;

public interface ReporterServerComponent {

	ID newToken (String... arg);

// final Map<String, String> values
	InstallationID registerInstallation (ID token) throws IOException;

	void startServer () throws IOException;

	String getInstanceID ();

	void updateSystemInfo (final ID token, Map<String, String> values) throws IOException;
}
