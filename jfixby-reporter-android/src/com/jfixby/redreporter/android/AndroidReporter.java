
package com.jfixby.redreporter.android;

import com.jfixby.redreporter.api.DeviceInfo;
import com.jfixby.redreporter.red.RedReporter;

public class AndroidReporter extends RedReporter {

	public AndroidReporter (final AndroidReporterConfig config) {
	}

	@Override
	public DeviceInfo getDeviceInfo () {
		return this.fill(super.getDeviceInfo());
	}

	private DeviceInfo fill (final DeviceInfo deviceInfo) {
		return deviceInfo;
	}

}
