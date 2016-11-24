
package com.jfixby.redreporter.server.core;

public class RedReporterServerConfig {

	private RedReporterDataBank bank;

	public void setRedReporterDataBank (final RedReporterDataBank bank) {
		this.bank = bank;
	}

	public RedReporterDataBank getRedReporterDataBank () {
		return this.bank;
	}

}
