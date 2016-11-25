
package com.jfixby.redreporter.server.core;

import java.io.IOException;

import com.jfixby.cmns.api.assets.ID;
import com.jfixby.cmns.api.err.Err;
import com.jfixby.redreporter.api.DeviceRegistration;
import com.jfixby.redreporter.server.api.DeviceIDGenerator;
import com.jfixby.redreporter.server.api.ReporterServerComponent;

public class RedReporterServer implements ReporterServerComponent {

	private final RedReporterDataBank bank;
	private DeviceIDGenerator idgen;
	private ServerSettings serverSettings;

	public RedReporterServer (final RedReporterServerConfig cfg) {
		this.bank = cfg.getRedReporterDataBank();

	}

	public void start () throws IOException {
		this.bank.connect();
		this.loadSettings();
		final String salt0 = this.serverSettings().getSalat0();
		this.idgen = new DeviceIDGenerator0(salt0);
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

	public void stop () {
		this.bank.disconnect();
	}

	public void onRegisterDevice (final ClientInput clientInput) {

// this.bank.get

	}

	public void testReg () {
		this.bank.testReg();

	}

	@Override
	public ID newDeviceID (final String... arg) {
		return this.idgen.newDeviceID(arg);
	}

	@Override
	public DeviceRegistration registerDevice (final ID deviceID) {
		Err.reportNotImplementedYet();
		return null;
	}

}
