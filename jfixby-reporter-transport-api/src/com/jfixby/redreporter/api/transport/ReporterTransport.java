
package com.jfixby.redreporter.api.transport;

import com.jfixby.cmns.api.ComponentInstaller;

public class ReporterTransport {

	static private ComponentInstaller<ReporterTransportComponent> componentInstaller = new ComponentInstaller<ReporterTransportComponent>(
		"ReporterTransport");

	public static final void installComponent (final ReporterTransportComponent component_to_install) {
		componentInstaller.installComponent(component_to_install);
	}

	public static final ReporterTransportComponent invoke () {
		return componentInstaller.invokeComponent();
	}

	public static final ReporterTransportComponent component () {
		return componentInstaller.getComponent();
	}

}
