
package com.jfixby.redreporter.api.transport;

import com.jfixby.redreporter.api.InstallationID;
import com.jfixby.redreporter.api.analytics.Report;

public interface ReporterTransportComponent {

	boolean sendReport (Report message);

	void pingServers ();

	InstallationID getInstallationID ();

	boolean deleteInstallationID ();

}
