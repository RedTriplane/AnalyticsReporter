
package com.jfixby.redreporter.api.transport;

import com.jfixby.cmns.api.sys.SystemInfo;
import com.jfixby.redreporter.api.InstallationID;
import com.jfixby.redreporter.api.Report;

public interface ReporterTransportComponent {

	InstallationID registerInstallation (final SystemInfo systemInfo);

	boolean sendReport (Report message);

	void pingServers ();

}
