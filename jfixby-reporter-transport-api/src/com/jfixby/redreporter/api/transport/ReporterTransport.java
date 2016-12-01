
package com.jfixby.redreporter.api.transport;

import com.jfixby.redreporter.api.analytics.Report;

public interface ReporterTransport {

	boolean sendReport (Report message);

	boolean resetInstallationID ();

	ServersCheckParams newServersCheckParams ();

	ServersCheck checkServers (ServersCheckParams params);

	ServersCheck checkServers ();

}
