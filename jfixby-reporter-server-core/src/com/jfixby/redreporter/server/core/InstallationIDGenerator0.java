
package com.jfixby.redreporter.server.core;

import com.jfixby.cmns.api.assets.ID;
import com.jfixby.cmns.api.assets.Names;
import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.List;
import com.jfixby.cmns.api.md5.MD5;

public class InstallationIDGenerator0 {

	private final String salt0;

	private final String instanceID;

	public static final long TIMESTAMP_OFFSET = System.currentTimeMillis();

	public InstallationIDGenerator0 (final String salt0, final String instanceID) {
		this.instanceID = instanceID;
		this.salt0 = salt0;
	}

	public ID newInstallationID (final String... args) {
		final List<String> list = Collections.newList(args);
		list.insertElementAt(this.instanceID, 0);
		list.add("" + (System.currentTimeMillis() - TIMESTAMP_OFFSET));
		return this.seal(list);
	}

	public ID seal (final List<String> list) {
		final ID prefix = Names.newAssetID(list);
		final String salt = this.applySalt(prefix.toString());
		final ID id = prefix.child(salt);
		return id;
	}

	public String applySalt (final String input) {
		return MD5.md5String(this.salt0 + input + this.salt0);
	}

}
