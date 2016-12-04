
package com.jfixby.redreporter.api.transport;

import com.jfixby.redreporter.api.analytics.Report;

public interface ReporterTransport {

	boolean resetInstallationID ();

	boolean submitReport (Report report);

}
