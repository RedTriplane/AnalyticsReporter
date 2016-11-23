
package com.jfixby.redreporter.android;

import com.jfixby.android.api.Android;
import com.jfixby.android.api.DisplayMetrics;
import com.jfixby.redreporter.api.DeviceInfo;
import com.jfixby.redreporter.api.TAGS;
import com.jfixby.redreporter.red.RedDeviceInfo;
import com.jfixby.redreporter.red.RedReporter;

public class AndroidReporter extends RedReporter {

	public AndroidReporter (final AndroidReporterConfig config) {
	}

	@Override
	public DeviceInfo getDeviceInfo () {
		return this.fill(new RedDeviceInfo());
	}

	private DeviceInfo fill (final RedDeviceInfo deviceInfo) {
		{
			final DisplayMetrics displayMetrics = Android.getDisplayMetrics();
			final int height = displayMetrics.getHeight();
			final int width = displayMetrics.getWidth();
			deviceInfo.putValue(TAGS.Android.Display.WIDTH, width);
			deviceInfo.putValue(TAGS.Android.Display.HEIGHT, height);
		}
		{
			final String brand = Android.getBrand();
			deviceInfo.putValue(TAGS.Android.Brand, brand);
		}
		{
			final String model = Android.getModel();
			deviceInfo.putValue(TAGS.Android.Model, model);
		}
		{
			final String release = Android.getVersionRelease();
			deviceInfo.putValue(TAGS.Android.Release, release);
		}
		{
			final String host = Android.getHost();
			deviceInfo.putValue(TAGS.Android.Host, host);
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
