
package com.jfixby.redreporter.server.core;

import java.io.IOException;

import com.jfixby.cmns.api.assets.ID;
import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.err.Err;
import com.jfixby.cmns.api.log.L;
import com.jfixby.redreporter.api.InstallationID;
import com.jfixby.redreporter.server.api.InstallationIDGenerator;
import com.jfixby.redreporter.server.api.ReporterServerComponent;

public class RedReporterServer implements ReporterServerComponent {

	private final RedReporterDataBank bank;
	private InstallationIDGenerator idgen;
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
	public void startServer () {
		try {
			this.bank.connect();
			this.loadSettings();
			final String salt0 = this.serverSettings().getSalat0();
			this.idgen = new InstallationIDGenerator0(salt0);
		} catch (final Throwable e) {
			Err.reportError(e);
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
		this.serverSettings.print();
	}

	public void stop () {
		this.bank.disconnect();
	}

	public void testReg () {
// this.bank.testReg();
	}

	@Override
	public ID newInstallationID (final String... arg) {
		return this.idgen.newInstallationID(arg);
	}

	@Override
	public InstallationID registerInstallation (final ID installID) throws IOException {
		L.d("register installation", installID);
		final InstallationID reg = this.bank.registerInstallation(installID);
		return reg;
	}

}
