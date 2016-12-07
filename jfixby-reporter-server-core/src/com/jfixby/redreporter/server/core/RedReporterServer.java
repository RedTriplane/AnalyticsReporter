
package com.jfixby.redreporter.server.core;

import java.io.IOException;

import com.jfixby.cmns.api.assets.ID;
import com.jfixby.cmns.api.collections.Map;
import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.log.L;
import com.jfixby.redreporter.api.ServerStatus;
import com.jfixby.redreporter.server.api.ReportStoreArguments;
import com.jfixby.redreporter.server.api.ReporterServerComponent;
import com.jfixby.redreporter.server.core.file.FileStorage;

public class RedReporterServer implements ReporterServerComponent {

	private final RedReporterDataBank bank;
	private final InstallationIDGenerator idgen;
	private final FileStorage fileStorage;

	public RedReporterServer (final RedReporterServerConfig cfg) {
		this.bank = cfg.getRedReporterDataBank();
		this.fileStorage = cfg.getReportsFileStorage();
		Debug.checkNull("bank", this.bank);
		Debug.checkNull("fileStorage", this.fileStorage);
		this.idgen = new InstallationIDGenerator(this.bank);
	}

	@Override
	public ServerStatus getStatus () {
		try {
			this.bank.readSettings();
			return ServerStatus.OK;
		} catch (final IOException e) {
			e.printStackTrace();
			return ServerStatus.ERROR;
		}
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

}
