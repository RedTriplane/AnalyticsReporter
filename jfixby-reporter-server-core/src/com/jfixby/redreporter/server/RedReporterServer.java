
package com.jfixby.redreporter.server;

import com.jfixby.cmns.api.assets.ID;
import com.jfixby.cmns.api.collections.Map;
import com.jfixby.cmns.api.err.Err;
import com.jfixby.redreporter.server.api.DB_STATE;
import com.jfixby.redreporter.server.api.ReportStoreArguments;
import com.jfixby.redreporter.server.api.ReporterServerComponent;
import com.jfixby.redreporter.server.api.STORAGE_STATE;
import com.jfixby.redreporter.server.api.ServerCoreConfig;
import com.jfixby.redreporter.server.core.RedReporterServerCore;

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
	public ReportStoreArguments newReportStoreArguments () {
		return this.core.newReportStoreArguments();
	}

	@Override
	public boolean storeReport (final ReportStoreArguments store_args) {
		return this.core.storeReport(store_args);
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

}
