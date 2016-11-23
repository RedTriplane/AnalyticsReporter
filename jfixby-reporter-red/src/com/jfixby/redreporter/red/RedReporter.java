
package com.jfixby.redreporter.red;

import com.jfixby.redreporter.api.DeviceInfo;
import com.jfixby.redreporter.api.ReporterComponent;
import com.jfixby.redreporter.api.ReporterErrorComponent;
import com.jfixby.redreporter.api.ReporterLogger;

public class RedReporter implements ReporterComponent {

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
