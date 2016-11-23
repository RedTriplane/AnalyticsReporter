
package com.jfixby.redreporter.api.transport;

import com.jfixby.redreporter.api.DeviceInfo;

public interface ReporterTransportComponent {

	DeviceRegistration registerDevice (final DeviceInfo deviceInfo);

}
