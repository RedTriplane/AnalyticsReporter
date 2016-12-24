
package com.jfixby.redreporter.server.core;

import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.Map;

public class ServerSettings {

	public ServerSettings () {
	}

	public static final String SALT_0 = "SALT_0";
	public static final String PARAMETER_NAME = "parameter_name";
	public static final String PARAMETER_VALUE = "parameter_value";
	public static final String TABLE_NAME = "server_settings";

	final Map<String, String> settings = Collections.newMap();

	public String getSalat0 () {
		return this.settings.get(SALT_0);
	}

	public void setSalt0 (final String salt) {
		this.settings.put(SALT_0, salt);
	}

	public void print () {
		this.settings.print("ServerSettings");
	}

}
