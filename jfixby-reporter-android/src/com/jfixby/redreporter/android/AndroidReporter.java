
package com.jfixby.redreporter.android;

import com.jfixby.android.api.Android;
import com.jfixby.android.api.DisplayMetrics;
import com.jfixby.redreporter.api.DeviceInfo;
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
			deviceInfo.putValue("Android.DisplayMetrics.width", width);
			deviceInfo.putValue("Android.DisplayMetrics.height", height);
		}
		{
			final String brand = Android.getBrand();
			deviceInfo.putValue("Android.brand", brand);
		}
		{
			final String model = Android.getModel();
			deviceInfo.putValue("Android.model", model);
		}
		{
			final String release = Android.getVersionRelease();
			deviceInfo.putValue("Android.release", release);
		}
		{
			final String host = Android.getHost();
			deviceInfo.putValue("Android.host", host);
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
