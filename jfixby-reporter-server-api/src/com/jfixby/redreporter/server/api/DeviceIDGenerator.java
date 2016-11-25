
package com.jfixby.redreporter.server.api;

import com.jfixby.cmns.api.assets.ID;

public interface DeviceIDGenerator {

	public ID newDeviceID (final String... args);

	public String applySalt (String input);

}
