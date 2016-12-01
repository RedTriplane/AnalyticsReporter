
package com.jfixby.redreporter.api.crash;

public interface CrashReporterComponent {

	void startService ();

	void stopService (boolean wait);

	void deployUncaughtExceptionHandler ();

	void deployErrorsListener ();

	void deployLogsListener ();

	void unDeployUncaughtExceptionHandler ();

	void unDeployErrorsListener ();

	void unDeployLogsListener ();

}
