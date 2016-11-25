
package com.jfixby.redreporter.server.core;

public class RedReporterServerConfig {

	private RedReporterDataBank bank;
	private String instance_id;

	public void setRedReporterDataBank (final RedReporterDataBank bank) {
		this.bank = bank;
	}

	public RedReporterDataBank getRedReporterDataBank () {
		return this.bank;
	}

	public void setInstanceID (final String instance_id) {
		this.instance_id = instance_id;
	}

	public String getInstanceID () {
		return this.instance_id;
	}

}
