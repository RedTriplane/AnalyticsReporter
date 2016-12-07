
package com.jfixby.redreporter.server.core;

import java.io.IOException;

import com.jfixby.cmns.api.assets.ID;
import com.jfixby.cmns.api.md5.MD5;

public class InstallationIDGenerator {

	private String salt0;

	private final RedReporterDataBank bank;

	public static final long TIMESTAMP_OFFSET = System.currentTimeMillis();

	public InstallationIDGenerator (final RedReporterDataBank bank) {
		this.bank = bank;
	}

	public String newInstallationID (ID prefix) throws IOException {
		prefix = prefix.child("ts-" + (System.currentTimeMillis() - TIMESTAMP_OFFSET));
		prefix = prefix.child("sl-0");
		final ID id = prefix.child(applySalt(prefix.toString(), this.salt0()));
		return id + "";
	}

	private String salt0 () throws IOException {
		if (this.salt0 != null) {
			return this.salt0;
		}
		this.salt0 = this.bank.readSettings().getSalat0();
		return this.salt0;
	}

	public static String applySalt (final String input, final String salt) {
		return MD5.md5String(salt + input + salt);
	}

}
