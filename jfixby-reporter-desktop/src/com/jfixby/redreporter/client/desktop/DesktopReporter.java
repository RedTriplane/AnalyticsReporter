
package com.jfixby.redreporter.client.desktop;

import com.jfixby.redreporter.api.transport.client.DeviceInfo;
import com.jfixby.redreporter.api.transport.client.ReporterComponent;
import com.jfixby.redreporter.api.transport.client.ReporterErrorComponent;
import com.jfixby.redreporter.api.transport.client.ReporterLogger;

public class DesktopReporter implements ReporterComponent {

	public DesktopReporter (final DesktopReporterConfig config) {
	}

	@Override
	public ReporterLogger getLogger () {
		return null;
	}

	@Override
	public ReporterErrorComponent getErr () {
		return null;
	}

	@Override
	public DeviceInfo getDeviceInfo () {
		return null;
	}

}
