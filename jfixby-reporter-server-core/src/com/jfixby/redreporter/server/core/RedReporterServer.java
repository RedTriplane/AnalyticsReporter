
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
	private InstallationIDGenerator0 idgen;
	private ServerSettings serverSettings;

	public RedReporterServer (final RedReporterServerConfig cfg) {
		this.bank = cfg.getRedReporterDataBank();
		Debug.checkNull("bank", this.bank);
	}

	@Override
	public ServerStatus getState () {
		try {
			this.checkStarted();
			this.bank.getServerSettings();
			return ServerStatus.OK;
		} catch (final IOException e) {
			e.printStackTrace();
			if (this.idgen != null) {
				return ServerStatus.ERROR;
			} else {
				return ServerStatus.STARTING;
			}
		}
	}

	synchronized private boolean checkStarted () throws IOException {
		if (this.idgen != null) {
			return true;
		}
		this.serverSettings = this.bank.getServerSettings();
		final String salt0 = this.serverSettings.getSalat0();
		this.idgen = new InstallationIDGenerator0(salt0);
		return false;
	}

	@Override
	public ID newToken (final ID prefix) {
		try {
			this.checkStarted();
			return this.idgen.newInstallationID(prefix);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public InstallationID registerInstallation (final ID token) {
		try {
			this.checkStarted();
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
			this.checkStarted();
			this.bank.updateSystemInfo(token, values);
			return true;
		} catch (final IOException e) {
			e.printStackTrace();
			return false;
		}
	}

}
