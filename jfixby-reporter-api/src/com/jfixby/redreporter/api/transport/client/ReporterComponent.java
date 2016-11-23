
package com.jfixby.redreporter.api.transport.client;

public interface ReporterComponent {

	ReporterLogger getLogger ();

	ReporterErrorComponent getErr ();

	DeviceInfo getDeviceInfo ();

}
