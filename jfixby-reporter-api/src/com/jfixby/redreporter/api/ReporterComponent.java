
package com.jfixby.redreporter.api;

public interface ReporterComponent {

	void startService ();

	void deployUncaughtExceptionHandler ();

	void deployErrorsListener ();

	void deployLogsListener ();

	Report newReport ();

}
