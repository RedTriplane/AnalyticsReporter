
package com.jfixby.redreporter.client.http;

import java.io.IOException;

import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.err.Err;
import com.jfixby.cmns.api.file.File;
import com.jfixby.redreporter.api.InstallationID;

public class InstallationIDStorage {
	public static final boolean CACHE_FOLDER_SUCCESSFULLY_CREATED = true;
	public static final boolean CACHE_FOLDER_IS_TEMPORARY = false;

	private final File iidStorage;
	private InstallationID iid;
	private static final String INSTALLATION_ID_FILE_NAME = "com.red-triplane.iid";

	public synchronized InstallationID getID () {
		if (this.iid != null) {
			return this.iid;
		}
		this.iid = this.readID();
		return this.iid;
	}

	public InstallationIDStorage (File iidStorage) {
		Debug.checkNull("InstallationIDStorageFolder", iidStorage);
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

	private boolean saveIID () {
		if (this.iidStorage == null) {
			return false;
		}
		final File iidFile = this.iidStorage.child(INSTALLATION_ID_FILE_NAME);
		try {
			iidFile.writeString(this.iid.token);
			return true;
		} catch (final IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public synchronized boolean deleteID () {
		this.iid = null;
		if (this.iidStorage == null) {
			return true;
		}
		final File iidFile = this.iidStorage.child(INSTALLATION_ID_FILE_NAME);
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
			iidFile.delete();
		} catch (final IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private InstallationID readID () {
		if (this.iidStorage == null) {
			return null;
		}
		final File iidFile = this.iidStorage.child(INSTALLATION_ID_FILE_NAME);
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

		final InstallationID iid = new InstallationID();
		iid.token = token;
		return iid;
	}

}
