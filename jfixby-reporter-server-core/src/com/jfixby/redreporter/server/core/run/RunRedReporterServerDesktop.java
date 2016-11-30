
package com.jfixby.redreporter.server.core.run;

import java.io.IOException;
import java.sql.SQLException;

import com.jfixby.cmns.adopted.gdx.json.RedJson;
import com.jfixby.cmns.api.assets.ID;
import com.jfixby.cmns.api.assets.Names;
import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.Map;
import com.jfixby.cmns.api.collections.Mapping;
import com.jfixby.cmns.api.json.Json;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.sys.Sys;
import com.jfixby.cmns.db.mysql.MySQL;
import com.jfixby.cmns.db.mysql.MySQLConfig;
import com.jfixby.red.desktop.DesktopSetup;
import com.jfixby.redreporter.server.core.BankSchema;
import com.jfixby.redreporter.server.core.RedReporterDataBank;
import com.jfixby.redreporter.server.core.RedReporterServer;
import com.jfixby.redreporter.server.core.RedReporterServerConfig;
import com.jfixby.redreporter.server.credentials.CONFIG;

public class RunRedReporterServerDesktop {

	public static void main (final String[] args) throws IOException, SQLException {
		DesktopSetup.deploy();
		Json.installComponent(new RedJson());

		final MySQLConfig config = new MySQLConfig();

		config.setServerName(CONFIG.DB_SERVER);
		config.setLogin(CONFIG.DB_LOGIN);
		config.setPassword(CONFIG.DB_PASSWORD);
		config.setDBName(CONFIG.DB_NAME);
		config.setUseSSL(!true);
		final MySQL mySQL = new MySQL(config);

		final RedReporterDataBank bank = new RedReporterDataBank(mySQL);

		final RedReporterServerConfig serveConfig = new RedReporterServerConfig();
		serveConfig.setRedReporterDataBank(bank);
		final RedReporterServer server = new RedReporterServer(serveConfig);

		final ID id = Names.newID("test-token-" + System.currentTimeMillis());
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
