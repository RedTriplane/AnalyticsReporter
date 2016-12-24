
package com.jfixby.redreporter.server.core.run;

import java.io.IOException;
import java.sql.SQLException;

import com.jfixby.redreporter.server.core.BankSchema;
import com.jfixby.redreporter.server.core.RedCoreConfig;
import com.jfixby.redreporter.server.core.RedReporterDataBank;
import com.jfixby.redreporter.server.core.RedReporterServerCore;
import com.jfixby.redreporter.server.credentials.CONFIG;
import com.jfixby.scarabei.api.assets.Names;
import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.Map;
import com.jfixby.scarabei.api.collections.Mapping;
import com.jfixby.scarabei.api.desktop.DesktopSetup;
import com.jfixby.scarabei.api.json.Json;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.sys.Sys;
import com.jfixby.scarabei.db.api.DB;
import com.jfixby.scarabei.db.api.DBConfig;
import com.jfixby.scarabei.db.api.DataBase;

public class RunRedReporterServerDesktop {

	public static void main (final String[] args) throws IOException, SQLException {
		DesktopSetup.deploy();
		Json.installComponent("com.jfixby.cmns.adopted.gdx.json.RedJson");

		final DBConfig config = DB.newDBConfig();

		config.setServerName(CONFIG.DB_SERVER);
		config.setLogin(CONFIG.DB_LOGIN);
		config.setPassword(CONFIG.DB_PASSWORD);
		config.setDBName(CONFIG.DB_NAME);
		config.setUseSSL(!true);
		final DataBase mySQL = DB.newDB(config);

		final RedReporterDataBank bank = new RedReporterDataBank(mySQL);

		final RedCoreConfig serveConfig = new RedCoreConfig();
		serveConfig.setRedReporterDataBank(bank);
		final RedReporterServerCore server = null;

		final String id = Names.newID("test-token-" + System.currentTimeMillis()).toString();
		L.d("id", id);
		server.registerInstallation(id);

		final Mapping<String, String> map = Sys.getSystemInfo().listParameters();
		final Map<String, String> trunced = Collections.newMap(map);
		trunced.remove(trunced.getKeyAt(0));
		trunced.remove(trunced.getKeyAt(0));

		server.updateSystemInfo(id, trunced);

		mySQL.getTable(BankSchema.SYSTEM_INFO.TableName).listAll().print("all");
		server.updateSystemInfo(id, Sys.getSystemInfo().listParameters());

		mySQL.getTable(BankSchema.SYSTEM_INFO.TableName).listAll().print("all+");

// server.testReg();

	}

}
