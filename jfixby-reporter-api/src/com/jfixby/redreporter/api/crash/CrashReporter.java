
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

	public static void deploy () {
		invoke().deploy();
	}

	public static void enableUncaughtExceptionHandler () {
		invoke().enableUncaughtExceptionHandler();
	}

	public static void enableErrorsListener () {
		invoke().enableErrorsListener();
	}

	public static void enableLogsListener () {
		invoke().enableLogsListener();
	}

	public static void disableUncaughtExceptionHandler () {
		invoke().disableUncaughtExceptionHandler();
	}

	public static void disableLogsListener () {
		invoke().disableLogsListener();
	}

	public static void disableErrorsListener () {
		invoke().disableErrorsListener();
	}

}
