
package com.jfixby.redreporter.server.api;

import com.jfixby.cmns.api.ComponentInstaller;
import com.jfixby.cmns.api.assets.ID;
import com.jfixby.cmns.api.collections.Map;
import com.jfixby.redreporter.api.ServerStatus;

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

	public static String registerInstallation (final ID token) {
		return invoke().registerInstallation(token);
	}

	public static boolean updateSystemInfo (final ID token, final Map<String, String> values) {
		return invoke().updateSystemInfo(token, values);
	}

	public static ServerStatus getStatus () {
		return invoke().getStatus();
	}

	public static ID newToken (final ID requestID) {
		return invoke().newToken(requestID);
	}
}
