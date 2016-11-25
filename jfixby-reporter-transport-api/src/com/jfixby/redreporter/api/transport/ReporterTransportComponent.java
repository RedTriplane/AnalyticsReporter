
package com.jfixby.redreporter.api.transport;

import com.jfixby.cmns.api.sys.SystemInfo;
import com.jfixby.redreporter.api.InstallationID;

public interface ReporterTransportComponent {

	InstallationID registerInstallation (final SystemInfo systemInfo);

}
