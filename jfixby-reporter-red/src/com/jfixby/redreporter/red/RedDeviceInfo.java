
package com.jfixby.redreporter.red;

import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.Map;
import com.jfixby.cmns.api.collections.Mapping;
import com.jfixby.redreporter.api.DeviceInfo;

public class RedDeviceInfo implements DeviceInfo {
	final Map<String, String> settings = Collections.newMap();

	@Override
	public Mapping<String, String> listParameters () {
		return this.settings;
	}

	@Override
	public void putValue (final String key, final Object value) {
		this.settings.put(key, "" + value);
	}

}
