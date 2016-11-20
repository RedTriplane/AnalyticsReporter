
package com.jfixby.redreporter.api;

import com.jfixby.cmns.api.ComponentInstaller;

public class RedReporter {

	static private ComponentInstaller<RedReporterComponent> componentInstaller = new ComponentInstaller<>("RedReporter");

	public static final void installComponent (final RedReporterComponent component_to_install) {
		componentInstaller.installComponent(component_to_install);
	}

	public static final RedReporterComponent invoke () {
		return componentInstaller.invokeComponent();
	}

	public static final RedReporterComponent component () {
		return componentInstaller.getComponent();
	}
}
