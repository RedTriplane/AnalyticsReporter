
package com.jfixby.redreporter.server.api;

import com.jfixby.cmns.api.ComponentInstaller;
import com.jfixby.cmns.api.assets.ID;
import com.jfixby.redreporter.api.DeviceRegistration;

public class ReporterServer {

	static private ComponentInstaller<ReporterServerComponent> componentInstaller = new ComponentInstaller<>("Angles");

	public static final void installComponent (final ReporterServerComponent component_to_install) {
		componentInstaller.installComponent(component_to_install);
	}

	public static final ReporterServerComponent invoke () {
		return componentInstaller.invokeComponent();
	}

	public static final ReporterServerComponent component () {
		return componentInstaller.getComponent();
	}

	public static DeviceRegistration registerDevice (final ID deviceID) {
		return invoke().registerDevice(deviceID);
	}
}
