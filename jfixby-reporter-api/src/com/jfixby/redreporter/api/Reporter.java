
package com.jfixby.redreporter.api;

import com.jfixby.cmns.api.ComponentInstaller;

public class Reporter {

	static private ComponentInstaller<ReporterComponent> componentInstaller = new ComponentInstaller<ReporterComponent>(
		"Reporter");

	public static final void installComponent (final ReporterComponent component_to_install) {
		componentInstaller.installComponent(component_to_install);
	}

	public static final ReporterComponent invoke () {
		return componentInstaller.invokeComponent();
	}

	public static final ReporterComponent component () {
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

}
