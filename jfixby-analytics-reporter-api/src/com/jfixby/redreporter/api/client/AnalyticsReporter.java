
package com.jfixby.redreporter.api.client;

import com.jfixby.cmns.api.ComponentInstaller;
import com.jfixby.redreporter.api.AnalyticsReporterAPI;

public class AnalyticsReporter {

	static private ComponentInstaller<AnalyticsReporterComponent> componentInstaller = new ComponentInstaller<AnalyticsReporterComponent>(
		"AnalyticsReporter");

	public static final void installComponent (final AnalyticsReporterComponent component_to_install) {
		componentInstaller.installComponent(component_to_install);
	}

	public static final AnalyticsReporterComponent invoke () {
		return componentInstaller.invokeComponent();
	}

	public static final AnalyticsReporterComponent component () {
		return componentInstaller.getComponent();
	}

	public static void pingServers () {
		invoke().pingServers();
	}

	public static AnalyticsReporterLoggerComponent getLogger () {
		return invoke().getLogger();
	}

	public static AnalyticsReporterErrorComponent getErr () {
		return invoke().getErr();
	}

	public static AnalyticsReporterAPI getAPI () {
		return invoke().getAPI();
	}
}
