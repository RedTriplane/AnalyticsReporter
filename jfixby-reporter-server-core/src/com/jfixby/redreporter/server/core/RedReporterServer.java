
package com.jfixby.redreporter.server.core;

import java.io.IOException;
import java.util.Map;

import com.jfixby.cmns.api.assets.ID;
import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.log.L;
import com.jfixby.redreporter.api.InstallationID;
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
	public void startServer () throws IOException {

		this.bank.connect();
		this.loadSettings();
		final String salt0 = this.serverSettings().getSalat0();
		this.idgen = new InstallationIDGenerator0(salt0);

	}

	private ServerSettings serverSettings () throws IOException {
		if (this.serverSettings == null) {
			this.loadSettings();
		}
		return this.serverSettings;
	}

	private void loadSettings () throws IOException {
		this.serverSettings = this.bank.getServerSettings();
		this.serverSettings.print();
	}

	public void stop () {
		this.bank.disconnect();
	}

	public void testReg () {
// this.bank.testReg();
	}

	@Override
	public ID newToken (final ID prefix) {
		return this.idgen.newInstallationID(prefix);
	}

	@Override
	public InstallationID registerInstallation (final ID token) throws IOException {
		L.d("register installation", token);
		final InstallationID reg = this.bank.registerInstallation(token);

		return reg;
	}

	@Override
	public void updateSystemInfo (final ID token, final Map<String, String> values) throws IOException {
		this.bank.updateSystemInfo(token, values);
	}

}
