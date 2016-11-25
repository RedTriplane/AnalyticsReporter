
package com.jfixby.redreporter.server.api;

import java.io.IOException;

import com.jfixby.cmns.api.assets.ID;
import com.jfixby.redreporter.api.InstallationID;

public interface ReporterServerComponent {

	ID newInstallationID (String... arg);

	InstallationID registerInstallation (ID installID) throws IOException;

	void startServer ();

	String getInstanceID ();
}
