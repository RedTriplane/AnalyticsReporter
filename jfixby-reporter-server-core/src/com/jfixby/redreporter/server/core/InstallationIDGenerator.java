
package com.jfixby.redreporter.server.core;

import com.jfixby.cmns.api.assets.ID;
import com.jfixby.cmns.api.md5.MD5;

public class InstallationIDGenerator {

	private final String salt0;

	public static final long TIMESTAMP_OFFSET = System.currentTimeMillis();

	public InstallationIDGenerator (final String salt0) {
		this.salt0 = salt0;
	}

	public ID newInstallationID (ID prefix) {
		prefix = prefix.child("ts-" + (System.currentTimeMillis() - TIMESTAMP_OFFSET));
		prefix = prefix.child("sl-0");
		final ID id = prefix.child(applySalt(prefix.toString(), this.salt0));
		return id;
	}

	public static String applySalt (final String input, final String salt) {
		return MD5.md5String(salt + input + salt);
	}

}
