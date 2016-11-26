
package com.jfixby.redreporter.server.api;

import java.io.IOException;
import java.util.Map;

import com.jfixby.cmns.api.ComponentInstaller;
import com.jfixby.cmns.api.assets.ID;
import com.jfixby.redreporter.api.InstallationID;

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

//
	public static InstallationID registerInstallation (final ID token) throws IOException {
		return invoke().registerInstallation(token);
	}

	public static void startServer () throws IOException {
		invoke().startServer();
	}

	public static void updateSystemInfo (final ID token, final Map<String, String> values) throws IOException {
		invoke().updateSystemInfo(token, values);
	}
}
