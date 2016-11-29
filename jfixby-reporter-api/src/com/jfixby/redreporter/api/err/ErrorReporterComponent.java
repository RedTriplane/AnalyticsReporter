
package com.jfixby.redreporter.api.err;

public interface ErrorReporterComponent {

	void startService ();

	void stopService ();

	void deployUncaughtExceptionHandler ();

	void deployErrorsListener ();

	void deployLogsListener ();

	void reportProblem (Throwable e);

	void reportCrash (Throwable e);

	void unDeployUncaughtExceptionHandler ();

	void unDeployErrorsListener ();

	void unDeployLogsListener ();

}
