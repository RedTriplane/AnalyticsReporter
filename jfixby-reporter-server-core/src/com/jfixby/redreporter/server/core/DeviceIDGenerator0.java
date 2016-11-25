
package com.jfixby.redreporter.server.core;

import com.jfixby.cmns.api.assets.ID;
import com.jfixby.cmns.api.assets.Names;
import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.List;
import com.jfixby.cmns.api.md5.MD5;
import com.jfixby.redreporter.server.api.DeviceIDGenerator;

public class DeviceIDGenerator0 implements DeviceIDGenerator {

	private final String salt0;

	public DeviceIDGenerator0 (final String salt0) {
		this.salt0 = salt0;
	}

	@Override
	public ID newDeviceID (final String... args) {
		final List<String> list = Collections.newList(args);
		final ID prefix = Names.newAssetID(list);
		final String salt = this.applySalt(prefix.toString());
		final ID id = prefix.child(salt);
		return id;
	}

	@Override
	public String applySalt (final String input) {
		return MD5.md5String(this.salt0 + input + this.salt0);
	}

}
