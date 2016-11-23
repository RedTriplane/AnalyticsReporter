
package com.jfixby.redreporter.api.transport.client;

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

	public static ReporterLogger getLogger () {
		return invoke().getLogger();
	}

	public static ReporterErrorComponent getErr () {
		return invoke().getErr();
	}

	public static DeviceInfo getDeviceInfo () {
		return invoke().getDeviceInfo();
	}

}
