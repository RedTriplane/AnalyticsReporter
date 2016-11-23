
package com.jfixby.redreporter.desktop;

import java.awt.Dimension;
import java.awt.Toolkit;

import com.jfixby.redreporter.api.DeviceInfo;
import com.jfixby.redreporter.red.RedDeviceInfo;
import com.jfixby.redreporter.red.RedReporter;

public class DesktopReporter extends RedReporter {

	public DesktopReporter (final DesktopReporterConfig config) {
	}

	@Override
	public DeviceInfo getDeviceInfo () {
		return this.fill(new RedDeviceInfo());
	}

	private DeviceInfo fill (final RedDeviceInfo deviceInfo) {
		{
			final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			final double width = screenSize.getWidth();
			final double height = screenSize.getHeight();
			deviceInfo.putValue("Desktop.ScreenSize.width", width);
			deviceInfo.putValue("Desktop.ScreenSize.height", height);
		}

		{
			final String osName = System.getProperty("os.name");
			deviceInfo.putValue("os.name", osName);
		}
		{
			final String java = System.getProperty("java.version");
			deviceInfo.putValue("java.version", java);
		}

		return deviceInfo;
	}
}
