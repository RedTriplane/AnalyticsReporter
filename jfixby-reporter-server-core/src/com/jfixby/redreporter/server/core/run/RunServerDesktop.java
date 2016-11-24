
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

public class RunServerDesktop {

	public static void main (final String[] args) throws IOException {
		DesktopSetup.deploy();
		Json.installComponent(new RedJson());

		final MySQLConfig config = new MySQLConfig();

		config.setDBUrlString(CONFIG.PROD.DB_URL_STRING);
		config.setLogin(CONFIG.PROD.DB_LOGIN);
		config.setPassword(CONFIG.PROD.DB_PASSWORD);

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
