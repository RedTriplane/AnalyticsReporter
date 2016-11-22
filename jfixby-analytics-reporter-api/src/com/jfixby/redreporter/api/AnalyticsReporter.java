
package com.jfixby.redreporter.api;

import com.jfixby.cmns.api.ComponentInstaller;

public class AnalyticsReporter {

	static private ComponentInstaller<AnalyticsReporterComponent> componentInstaller = new ComponentInstaller<>("AnalyticsReporter");

	public static final void installComponent (final AnalyticsReporterComponent component_to_install) {
		componentInstaller.installComponent(component_to_install);
	}

	public static final AnalyticsReporterComponent invoke () {
		return componentInstaller.invokeComponent();
	}

	public static final AnalyticsReporterComponent component () {
		return componentInstaller.getComponent();
	}
}