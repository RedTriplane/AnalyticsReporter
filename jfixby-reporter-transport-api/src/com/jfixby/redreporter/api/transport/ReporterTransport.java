
package com.jfixby.redreporter.api.transport;

public interface ReporterTransport {

	boolean resetInstallationID ();

	ReportWriter newReportWriter ();

}
