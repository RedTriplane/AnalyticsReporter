
package com.jfixby.redreporter.server.core.run;

import java.io.IOException;

import com.jfixby.cmns.adopted.gdx.json.RedJson;
import com.jfixby.cmns.api.json.Json;
import com.jfixby.cmns.db.mysql.MySQL;
import com.jfixby.cmns.db.mysql.MySQLConfig;
import com.jfixby.red.desktop.DesktopSetup;
import com.jfixby.redreporter.server.core.RedReporterDataBank;
import com.jfixby.redreporter.server.core.RedReporterServer;
import com.jfixby.redreporter.server.core.RedReporterServerConfig;
import com.jfixby.redreporter.server.credentials.CONFIG;

public class RunRedReporterServerDesktop {

	public static void main (final String[] args) throws IOException {
		DesktopSetup.deploy();
		Json.installComponent(new RedJson());

		final MySQLConfig config = new MySQLConfig();

		config.setServerName(CONFIG.LOCALHOST);
		config.setLogin(CONFIG.DB_LOGIN);
		config.setPassword(CONFIG.DB_PASSWORD);
		config.setDBName(CONFIG.DB_NAME);
		config.setUseSSL(!true);

		final MySQL mySQL = new MySQL(config);

		final RedReporterDataBank bank = new RedReporterDataBank(mySQL);

		final RedReporterServerConfig serveConfig = new RedReporterServerConfig();
		serveConfig.setRedReporterDataBank(bank);

		final RedReporterServer server = new RedReporterServer(serveConfig);
		server.start();

		server.testReg();

		server.stop();

	}

}
