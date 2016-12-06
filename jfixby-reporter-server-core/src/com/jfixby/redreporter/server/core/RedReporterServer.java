
package com.jfixby.redreporter.server.core;

import java.io.IOException;

import com.jfixby.cmns.api.assets.ID;
import com.jfixby.cmns.api.collections.Map;
import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.log.L;
import com.jfixby.redreporter.api.InstallationID;
import com.jfixby.redreporter.api.ServerStatus;
import com.jfixby.redreporter.server.api.ReporterServerComponent;

public class RedReporterServer implements ReporterServerComponent {

	private final RedReporterDataBank bank;
	private final InstallationIDGenerator idgen;

	public RedReporterServer (final RedReporterServerConfig cfg) {
		this.bank = cfg.getRedReporterDataBank();
		Debug.checkNull("bank", this.bank);
		this.idgen = new InstallationIDGenerator(this.bank);
	}

	@Override
	public ServerStatus getStatus () {
		try {
			this.bank.readSettings();
			return ServerStatus.OK;
		} catch (final IOException e) {
			e.printStackTrace();
			return ServerStatus.ERROR;
		}
	}

	@Override
	public ID newToken (final ID prefix) {
		try {
			return this.idgen.newInstallationID(prefix);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public InstallationID registerInstallation (final ID token) {
		try {
			L.d("register installation", token);
			InstallationID reg;
			reg = this.bank.registerInstallation(token);
			return reg;
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean updateSystemInfo (final ID token, final Map<String, String> values) {
		try {
			this.bank.updateSystemInfo(token, values);
			return true;
		} catch (final IOException e) {
			e.printStackTrace();
			return false;
		}
	}

}
