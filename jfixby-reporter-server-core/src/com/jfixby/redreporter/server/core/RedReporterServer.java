
package com.jfixby.redreporter.server.core;

import java.io.IOException;

import com.jfixby.cmns.api.assets.ID;
import com.jfixby.cmns.api.collections.Map;
import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.log.L;
import com.jfixby.redreporter.api.InstallationID;
import com.jfixby.redreporter.server.api.ReporterServerComponent;
import com.jfixby.redreporter.server.api.ServerState;

public class RedReporterServer implements ReporterServerComponent {

	private final RedReporterDataBank bank;
	private InstallationIDGenerator0 idgen;
	private ServerSettings serverSettings;

	public RedReporterServer (final RedReporterServerConfig cfg) {
		this.bank = cfg.getRedReporterDataBank();
		Debug.checkNull("bank", this.bank);
	}

	@Override
	public ServerState getState () {
		if (this.idgen != null) {
			try {
				this.loadSettings();
				return ServerState.OK;
			} catch (final IOException e) {
				e.printStackTrace();
				return ServerState.ERROR;
			}
		} else {
			try {
				this.checkStarted();
				return ServerState.OK;
			} catch (final IOException e) {
				e.printStackTrace();
				return ServerState.STARTING;
			}
		}

	}

	synchronized private void checkStarted () throws IOException {
		if (this.idgen != null) {
			return;
		}
		this.loadSettings();
		final String salt0 = this.serverSettings().getSalat0();
		this.idgen = new InstallationIDGenerator0(salt0);
		return;
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

	private ServerSettings serverSettings () throws IOException {
		if (this.serverSettings == null) {
			this.loadSettings();
		}
		return this.serverSettings;
	}

	private void loadSettings () throws IOException {
		this.serverSettings = this.bank.getServerSettings();
	}

}
