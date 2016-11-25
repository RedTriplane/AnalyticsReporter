
package com.jfixby.redreporter.server.api;

import java.io.IOException;

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

	public static InstallationID registerInstallation (final ID installID) throws IOException {
		return invoke().registerInstallation(installID);
	}

	public static void startServer () {
		invoke().startServer();
	}

	public static String getInstanceID () {
		return invoke().getInstanceID();
	}
}
