
package com.jfixby.redreporter.api.crash;

public interface CrashReporterComponent {

	void enableUncaughtExceptionHandler ();

	void enableErrorsListener ();

	void enableLogsListener ();

	void disableUncaughtExceptionHandler ();

	void disableErrorsListener ();

	void disableLogsListener ();

	void deploy ();

	boolean isUncaughtExceptionHandlerEnabled ();

	boolean isErrorsListenerEnabled ();

	boolean isLogsListenerEnabled ();

}
