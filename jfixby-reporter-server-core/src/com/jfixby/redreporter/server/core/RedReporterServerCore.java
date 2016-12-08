
package com.jfixby.redreporter.server.core;

import java.io.IOException;

import com.jfixby.cmns.api.assets.ID;
import com.jfixby.cmns.api.collections.Map;
import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.aws.api.s3.S3CredentialsProvider;
import com.jfixby.cmns.db.api.DataBase;
import com.jfixby.redreporter.server.api.DB_STATE;
import com.jfixby.redreporter.server.api.ReportStoreArguments;
import com.jfixby.redreporter.server.api.STORAGE_STATE;
import com.jfixby.redreporter.server.api.ServerCoreConfig;
import com.jfixby.redreporter.server.core.file.FileStorage;
import com.jfixby.redreporter.server.core.file.FileStorageConfig;
import com.jfixby.redreporter.server.credentials.CONFIG;

public class RedReporterServerCore implements ServerCore {

	private final RedReporterDataBank bank;
	private final InstallationIDGenerator idgen;
	private final FileStorage fileStorage;

	final S3CredentialsProvider s3CredentialsProvider = new S3CredentialsProvider() {
		@Override
		public String getAccessKeyID () {
			final String key = System.getenv("S3_ACCESS_KEY_ID");
			if (key == null) {
				return CONFIG.S3_RR1_ACCESS_KEY;
			}
			return key;
		}

		@Override
		public String getSecretKeyID () {
			final String key = System.getenv("S3_SECRET_KEY_ID");
			if (key == null) {
				return CONFIG.S3_RR1_SECRET_KEY;
			}
			return key;
		}
	};

	public RedReporterServerCore (final ServerCoreConfig cfg) {
		final DataBase dataBase = Debug.checkNull("getDataBase()", cfg.getDataBase());
		this.bank = new RedReporterDataBank(dataBase);
		final FileStorageConfig fsConfig = new FileStorageConfig();

		final String buckeName = Debug.checkNull("getBucketName()", cfg.getBucketName());
		fsConfig.setBucketName(buckeName);
		fsConfig.setS3CredentialsProvider(this.s3CredentialsProvider);
		this.fileStorage = new FileStorage(fsConfig);
		Debug.checkNull("bank", this.bank);
		Debug.checkNull("fileStorage", this.fileStorage);
		this.idgen = new InstallationIDGenerator(this.bank);
	}

	@Override
	public String newToken (final ID prefix) {
		try {
			return this.idgen.newInstallationID(prefix);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String registerInstallation (final String token) {
		try {
			L.d("register installation", token);
			String reg;
			reg = this.bank.registerInstallation(token);
			return reg;
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean updateSystemInfo (final String token, final Map<String, String> values) {
		try {
			this.bank.updateSystemInfo(token, values);
			return true;
		} catch (final IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public ReportStoreArguments newReportStoreArguments () {
		return new RedReportStoreArguments();
	}

	@Override
	public Long findInstallation (final String token) {
		try {
			return this.bank.findIDForToken(token);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean storeReport (final ReportStoreArguments store_args) {
		Debug.checkNull("receivedTimestamp", store_args.getReceivedTimeStamp());
		Debug.checkNull("sentTimestamp", store_args.getSentTimestamp());
		Debug.checkNull("writtenTimestamp", store_args.getWrittenTimestamp());
		Debug.checkNull("installID", store_args.getInstallID());
		Debug.checkNull("versionString", store_args.getVersionString());
		Debug.checkNull("fileName", store_args.getFileID());
		Debug.checkNull("resializedBody", store_args.getReportData());
		File logFile = null;
		try {
			logFile = this.fileStorage.storeReport(store_args);
		} catch (final IOException e) {
			L.e(e);
			return false;
		}
		try {
			this.bank.storeReport(store_args, logFile);
		} catch (final IOException e) {
			L.e(e);
			return false;
		}
		return true;
	}

	@Override
	public DB_STATE getDBState () {
		try {
			this.bank.readSettings();
			return DB_STATE.OK;
		} catch (final IOException e) {
			e.printStackTrace();
			return DB_STATE.ERROR;
		}

	}

	@Override
	public STORAGE_STATE getSorageState () {
		return this.fileStorage.getState();
	}

}
