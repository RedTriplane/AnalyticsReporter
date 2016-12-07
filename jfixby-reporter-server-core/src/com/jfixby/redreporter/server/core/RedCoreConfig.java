
package com.jfixby.redreporter.server.core;

import com.jfixby.redreporter.server.core.file.FileStorage;

public class RedCoreConfig {

	private RedReporterDataBank bank;
	private FileStorage fileStorage;

	public void setRedReporterDataBank (final RedReporterDataBank bank) {
		this.bank = bank;
	}

	public RedReporterDataBank getRedReporterDataBank () {
		return this.bank;
	}

	public void setReportsFileStorage (final FileStorage fileStorage) {
		this.fileStorage = fileStorage;
	}

	public FileStorage getReportsFileStorage () {
		return this.fileStorage;
	}

}
