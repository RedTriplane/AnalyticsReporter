
package com.jfixby.redreporter.api.transport;

import com.jfixby.cmns.api.sys.SystemInfo;
import com.jfixby.redreporter.api.DeviceRegistration;

public interface ReporterTransportComponent {

	DeviceRegistration registerDevice (final SystemInfo deviceInfo);

}
