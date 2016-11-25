
package com.jfixby.redreporter.server.core;

public class ServerSettings {

	public static final String SALT_0 = "SALT_0";
	public static final String PARAMETER_NAME = "parameter_name";
	public static final String PARAMETER_VALUE = "parameter_value";
	public static final String TABLE_NAME = "server_settings";
	private String salt0;

	public String getSalat0 () {
		return this.salt0;
	}

	public void setSalt0 (final String salt) {
		this.salt0 = salt;
	}

}
