
package com.jfixby.redreporter.api.transport;

import com.jfixby.redreporter.api.transport.client.DeviceInfo;

public interface ReporterTransportComponent {

	DeviceRegistration registerDevice (final DeviceInfo deviceInfo);

}
