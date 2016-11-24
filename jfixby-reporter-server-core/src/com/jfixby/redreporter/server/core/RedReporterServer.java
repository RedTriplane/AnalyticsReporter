
package com.jfixby.redreporter.server.core;

import java.io.IOException;

public class RedReporterServer {

	private final RedReporterDataBank bank;

	public RedReporterServer (final RedReporterServerConfig cfg) {
		this.bank = cfg.getRedReporterDataBank();
	}

	public void start () throws IOException {
		this.bank.connect();
	}

	public void stop () {
		this.bank.disconnect();
	}

	public void onRegisterDevice (final ClientInput clientInput) {

// this.bank.get

	}

}
