
package com.jfixby.redreporter.api.transport;

import com.jfixby.cmns.api.sys.SystemInfo;

public interface ReporterTransportComponent {

	DeviceRegistration registerDevice (final SystemInfo deviceInfo);

}
