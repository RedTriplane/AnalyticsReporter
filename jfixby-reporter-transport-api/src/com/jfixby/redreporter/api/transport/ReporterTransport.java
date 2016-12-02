
package com.jfixby.redreporter.api.transport;

import com.jfixby.cmns.api.collections.Mapping;
import com.jfixby.redreporter.api.analytics.Report;

public interface ReporterTransport {

	boolean resetInstallationID ();

	ServersCheckParams newServersCheckParams ();

	ServersCheck checkServers (ServersCheckParams params);

	ServersCheck checkServers ();

	boolean sendReport (Report report, Mapping<String, String> params);

}
