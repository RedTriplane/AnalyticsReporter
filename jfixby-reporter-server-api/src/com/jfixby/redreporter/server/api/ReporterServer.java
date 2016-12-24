
package com.jfixby.redreporter.server.api;

import com.jfixby.scarabei.api.ComponentInstaller;
import com.jfixby.scarabei.api.assets.ID;
import com.jfixby.scarabei.api.collections.Map;

public class ReporterServer {

	static private ComponentInstaller<ReporterServerComponent> componentInstaller = new ComponentInstaller<>("Angles");

	public static final void installComponent (final ReporterServerComponent component_to_install) {
		componentInstaller.installComponent(component_to_install);
	}

	public static void installComponent (final String className) {
		componentInstaller.installComponent(className);
	}

	public static final ReporterServerComponent invoke () {
		return componentInstaller.invokeComponent();
	}

	public static final ReporterServerComponent component () {
		return componentInstaller.getComponent();
	}

	public static ServerCoreConfig newReporterServerConfig () {
		return invoke().newReporterServerConfig();
	}

	public static void deployCore (final ServerCoreConfig coreConfig) {
		invoke().deployCore(coreConfig);
	}

	public static DB_STATE getDBState () {
		return invoke().getDBState();
	}

	public static STORAGE_STATE getStorageState () {
		return invoke().getSorageState();
	}

	public static Long findInstallationID (final String token) {
		return invoke().findInstallationID(token);
	}

	public static ReportFileStoreArguments newReportFileStoreArguments () {
		return invoke().newReportFileStoreArguments();
	}

	public static boolean storeReportFile (final ReportFileStoreArguments store_args) {
		return invoke().storeReportFile(store_args);
	}

	public static String registerInstallation (final String token) {
		return invoke().registerInstallation(token);
	}

	public static String newToken (final ID requestID) {
		return invoke().newToken(requestID);
	}

	public static boolean updateSystemInfo (final String token_string, final Map<String, String> params) {
		return invoke().updateSystemInfo(token_string, params);
	}

	public static void reportDeserializationtionProblem (final Throwable e) {
		invoke().reportDeserializationtionProblem(e);
	}

	public static boolean registerReport (final ReportRegistration reg) {
		return invoke().registerReport(reg);
	}

	public static ReportRegistration newReportRegistration () {
		return invoke().newReportRegistration();
	}

}
