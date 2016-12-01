
package com.jfixby.redreporter.api.crash;

import com.jfixby.cmns.api.ComponentInstaller;

public class CrashReporter {

	static private ComponentInstaller<CrashReporterComponent> componentInstaller = new ComponentInstaller<CrashReporterComponent>(
		"CrashReporterComponent");

	public static final void installComponent (final CrashReporterComponent component_to_install) {
		componentInstaller.installComponent(component_to_install);
	}

	public static final CrashReporterComponent invoke () {
		return componentInstaller.invokeComponent();
	}

	public static final CrashReporterComponent component () {
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

	public static void stopService (final boolean wait) {
		invoke().stopService(wait);
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

}
