
package com.jfixby.redreporter.server;

import com.jfixby.redreporter.server.api.DB_STATE;
import com.jfixby.redreporter.server.api.ReportFileStoreArguments;
import com.jfixby.redreporter.server.api.ReportRegistration;
import com.jfixby.redreporter.server.api.ReporterServerComponent;
import com.jfixby.redreporter.server.api.STORAGE_STATE;
import com.jfixby.redreporter.server.api.ServerCoreConfig;
import com.jfixby.redreporter.server.core.RedReporterServerCore;
import com.jfixby.scarabei.api.assets.ID;
import com.jfixby.scarabei.api.collections.Map;
import com.jfixby.scarabei.api.err.Err;

public class RedReporterServer implements ReporterServerComponent {

	private RedReporterServerCore core;

	@Override
	public ServerCoreConfig newReporterServerConfig () {
		return new RedServerCoreConfig();
	}

	@Override
	public void deployCore (final ServerCoreConfig coreConfig) {
		if (this.core != null) {
			Err.reportError("Core already deployed " + this.core);
		}

		this.core = new RedReporterServerCore(coreConfig);
	}

	@Override
	public Long findInstallationID (final String token) {
		return this.core.findInstallation(token);
	}

	@Override
	public ReportFileStoreArguments newReportFileStoreArguments () {
		return this.core.newReportFileStoreArguments();
	}

	@Override
	public boolean storeReportFile (final ReportFileStoreArguments store_args) {
		return this.core.storeReportFile(store_args);
	}

	@Override
	public String registerInstallation (final String token) {
		return this.core.registerInstallation(token);
	}

	@Override
	public String newToken (final ID requestID) {
		return this.core.newToken(requestID);
	}

	@Override
	public boolean updateSystemInfo (final String token_string, final Map<String, String> params) {
		return this.core.updateSystemInfo(token_string, params);
	}

	@Override
	public DB_STATE getDBState () {
		return this.core.getDBState();
	}

	@Override
	public STORAGE_STATE getSorageState () {
		return this.core.getSorageState();
	}

	@Override
	public void reportDeserializationtionProblem (final Throwable e) {
		this.core.reportDeserializationtionProblem(e);
	}

	@Override
	public boolean registerReport (final ReportRegistration reg) {
		return this.core.registerReport(reg);
	}

	@Override
	public ReportRegistration newReportRegistration () {
		return this.core.newReportRegistration();
	}

}
