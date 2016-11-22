
package com.jfixby.redreporter.api;

import com.jfixby.cmns.api.ComponentInstaller;

public class AnalyticsReporter {

	static private ComponentInstaller<AnalyticsReporterAPI> componentInstaller = new ComponentInstaller<AnalyticsReporterAPI>(
		"AnalyticsReporter");

	public static final void installComponent (final AnalyticsReporterAPI component_to_install) {
		componentInstaller.installComponent(component_to_install);
	}

	public static final AnalyticsReporterAPI invoke () {
		return componentInstaller.invokeComponent();
	}

	public static final AnalyticsReporterAPI component () {
		return componentInstaller.getComponent();
	}

}
