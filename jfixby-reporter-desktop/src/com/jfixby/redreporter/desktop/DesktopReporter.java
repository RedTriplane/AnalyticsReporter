
package com.jfixby.redreporter.desktop;

import java.awt.Dimension;
import java.awt.Toolkit;

import com.jfixby.redreporter.api.DeviceInfo;
import com.jfixby.redreporter.api.TAGS;
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
			deviceInfo.putValue(TAGS.Desktop.Screen.WIDTH, width);
			deviceInfo.putValue(TAGS.Desktop.Screen.HEIGHT, height);
		}

		{
			final String osName = System.getProperty("os.name");
			deviceInfo.putValue(TAGS.System.OS_NAME, osName);
		}
		{
			final String java = System.getProperty("java.version");
			deviceInfo.putValue(TAGS.Java.Version, java);
		}

		return deviceInfo;
	}
}
