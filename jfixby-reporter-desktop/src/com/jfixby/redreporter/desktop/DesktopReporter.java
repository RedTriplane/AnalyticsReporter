
package com.jfixby.redreporter.desktop;

import com.jfixby.redreporter.api.DeviceInfo;
import com.jfixby.redreporter.red.RedReporter;

public class DesktopReporter extends RedReporter {

	public DesktopReporter (final DesktopReporterConfig config) {
	}

	@Override
	public DeviceInfo getDeviceInfo () {
		return this.fill(super.getDeviceInfo());
	}

	private DeviceInfo fill (final DeviceInfo deviceInfo) {
		return deviceInfo;
	}
}
