
package com.jfixby.redreporter.glassfish;

import com.jfixby.redreporter.server.api.HealthReportType;
import com.jfixby.scarabei.api.log.L;

public class TestClassLoader {

	public static void main (final String[] args) {
		final RedReporterEntryPointArguments arg = new RedReporterEntryPointArguments();
		final String healthReport = RedReporterEntryPoint.getHealthReport(HealthReportType.LATEST, arg);
		L.d(healthReport);
	}

}
