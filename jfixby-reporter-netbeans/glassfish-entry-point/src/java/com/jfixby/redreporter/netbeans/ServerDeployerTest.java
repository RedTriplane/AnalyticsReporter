package com.jfixby.redreporter.netbeans;


import com.jfixby.redreporter.glassfish.RedReporterEntryPoint;
import com.jfixby.redreporter.glassfish.RedReporterEntryPointArguments;
import com.jfixby.redreporter.server.api.HealthReportType;
import com.jfixby.scarabei.api.log.L;

public class ServerDeployerTest {

	public static final void main (final String[] ars) {

		final RedReporterEntryPointArguments arg = new RedReporterEntryPointArguments();
		final String healthReport = RedReporterEntryPoint.getHealthReport(HealthReportType.LATEST, arg);
		L.d(healthReport);
	}

}
