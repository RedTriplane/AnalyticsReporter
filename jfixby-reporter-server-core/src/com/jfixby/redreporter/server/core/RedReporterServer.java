
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
	private final String instance_id;

	public RedReporterServer (final RedReporterServerConfig cfg) {
		this.bank = cfg.getRedReporterDataBank();
		this.instance_id = cfg.getInstanceID();
		Debug.checkNull("instance_id", this.instance_id);
		Debug.checkNull("bank", this.bank);
	}

	@Override
	public String getInstanceID () {
		return this.instance_id;
	}

	@Override
	public void startServer () throws IOException {

		this.bank.connect();
		this.loadSettings();
		final String salt0 = this.serverSettings().getSalat0();
		this.idgen = new InstallationIDGenerator0(salt0, this.getInstanceID());

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
	public ID newToken (final String... arg) {
		return this.idgen.newInstallationID(arg);
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
