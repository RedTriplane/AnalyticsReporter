
package com.jfixby.redreporter.api.transport;

import com.jfixby.redreporter.api.analytics.ReportWriter;

public interface ReporterTransport {

	boolean resetInstallationID ();

	ReportWriter newReportWriter ();

}
