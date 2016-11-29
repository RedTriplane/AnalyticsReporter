
package com.jfixby.redreporter.api.err;

import com.jfixby.cmns.api.ComponentInstaller;

public class ErrorReporter {

	static private ComponentInstaller<ErrorReporterComponent> componentInstaller = new ComponentInstaller<ErrorReporterComponent>(
		"ErrorReporter");

	public static final void installComponent (final ErrorReporterComponent component_to_install) {
		componentInstaller.installComponent(component_to_install);
	}

	public static final ErrorReporterComponent invoke () {
		return componentInstaller.invokeComponent();
	}

	public static final ErrorReporterComponent component () {
		return componentInstaller.getComponent();
	}

	public static void startService () {
		invoke().startService();
	}

	public static void deployUncaughtExceptionHandler () {
		invoke().deployUncaughtExceptionHandler();
	}

	public static void deployErrorsListener () {
		invoke().deployErrorsListener();
	}

	public static void deployLogsListener () {
		invoke().deployLogsListener();
	}

	public static void stopService () {
		invoke().stopService();
	}

	public static void unDeployUncaughtExceptionHandler () {
		invoke().unDeployUncaughtExceptionHandler();
	}

	public static void unDeployErrorsListener () {
		invoke().unDeployErrorsListener();
	}

	public static void unDeployLogsListener () {
		invoke().unDeployLogsListener();
	}

	public static void reportCrash (final Throwable e) {
		invoke().reportCrash(e);
	}

	public static void reportProblem (final Throwable e) {
		invoke().reportProblem(e);
	}

}
