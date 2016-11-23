
package com.jfixby.redreporter.api;

import com.jfixby.cmns.api.collections.Mapping;

public interface DeviceInfo {

	Mapping<String, String> listParameters ();

	void putValue (String key, Object value);

}
