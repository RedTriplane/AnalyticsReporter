
package com.jfixby.redreporter.glassfish;

import com.jfixby.cmns.api.log.L;
import com.jfixby.redreporter.server.api.HealthReportType;

public class ServerDeployerTest {

	public static final void main (final String[] ars) {

		final RedReporterEntryPointArguments arg = new RedReporterEntryPointArguments();
		final String healthReport = RedReporterEntryPoint.getHealthReport(HealthReportType.LATEST, arg);
		L.d(healthReport);
	}

}
