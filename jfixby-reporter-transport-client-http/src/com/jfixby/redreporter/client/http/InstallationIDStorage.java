
package com.jfixby.redreporter.client.http;

import java.io.IOException;

import com.jfixby.scarabei.api.debug.Debug;
import com.jfixby.scarabei.api.err.Err;
import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.log.L;

public class InstallationIDStorage {
	public static final boolean CACHE_FOLDER_SUCCESSFULLY_CREATED = true;
	public static final boolean CACHE_FOLDER_IS_TEMPORARY = false;

	private final File iidStorage;
	private String iid;
	private final String installationIDFileName;

	public synchronized String getID () {
		if (this.iid != null) {
			return this.iid;
		}
		this.iid = this.readID();
		return this.iid;
	}

	public InstallationIDStorage (File iidStorage, final String installationIDFileName) {
		Debug.checkNull("InstallationIDStorageFolder", iidStorage);
		Debug.checkEmpty("installationIDFileName", installationIDFileName);
		this.installationIDFileName = Debug.checkNull("installationIDFileName", installationIDFileName);

		try {
			iidStorage.makeFolder();
			iidStorage.checkExists();
			iidStorage.checkIsFolder();
		} catch (final Throwable e) {
			iidStorage = null;
			Err.reportError(e);
		}
		this.iidStorage = iidStorage;
	}

	public synchronized boolean setID (final String token) {
		if (this.iid == null || !this.iid.equals(token)) {
			this.iid = token;
			final File iidFile = this.iidStorage.child(this.installationIDFileName);
			try {
				L.d("writing", iidFile);
				iidFile.writeString(this.iid);
				return true;
			} catch (final IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	public synchronized boolean deleteID () {
		this.iid = null;
		if (this.iidStorage == null) {
			return true;
		}
		final File iidFile = this.iidStorage.child(this.installationIDFileName);
		try {
			if (!iidFile.exists()) {
				return false;
			}
		} catch (final IOException e) {
			e.printStackTrace();
			return false;
		}
		try {
			if (!iidFile.isFile()) {
				return false;
			}
		} catch (final IOException e) {
			e.printStackTrace();
			return false;
		}

		try {

			L.d("deleting", iidFile);
			iidFile.delete();
		} catch (final IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private String readID () {
		if (this.iidStorage == null) {
			return null;
		}
		final File iidFile = this.iidStorage.child(this.installationIDFileName);
		try {
			if (!iidFile.exists()) {
				return null;
			}
		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		}
		try {
			if (!iidFile.isFile()) {
				return null;
			}
		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		}

		String token;
		try {

			L.d("reading", iidFile);
			token = iidFile.readToString();
		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		}
		if (token == null) {
			return null;
		}

		if ("".equals(token)) {
			return null;
		}

		return token;
	}

}
