
package com.jfixby.redreporter.api.transport;

import com.jfixby.redreporter.api.InstallationID;
import com.jfixby.redreporter.api.analytics.Report;

public interface ReporterTransportComponent {

	boolean sendReport (Report message);

	ServersCheck checkServers (ServersCheckParams params);

	InstallationID getInstallationID ();

	boolean deleteInstallationID ();

	ServersCheckParams newServersCheckParams ();

	ServersCheck checkServers ();

}
