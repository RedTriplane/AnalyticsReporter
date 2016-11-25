
package com.jfixby.redreporter.server.api;

import com.jfixby.cmns.api.assets.ID;
import com.jfixby.redreporter.api.DeviceRegistration;

public interface ReporterServerComponent {

	ID newDeviceID (String... arg);

	DeviceRegistration registerDevice (ID deviceID);
}
