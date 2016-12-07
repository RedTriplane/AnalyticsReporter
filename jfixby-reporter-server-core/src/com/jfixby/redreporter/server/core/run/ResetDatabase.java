
package com.jfixby.redreporter.server.core.run;

import java.io.IOException;
import java.sql.SQLException;

import com.jfixby.cmns.api.desktop.DesktopSetup;
import com.jfixby.cmns.api.json.Json;
import com.jfixby.cmns.api.sys.Sys;
import com.jfixby.cmns.db.api.DB;
import com.jfixby.cmns.db.api.DBConfig;
import com.jfixby.cmns.db.api.DataBase;
import com.jfixby.redreporter.server.core.RedReporterDataBank;
import com.jfixby.redreporter.server.credentials.CONFIG;

public class ResetDatabase {

	public static void main (final String[] args) throws IOException, SQLException {
		DesktopSetup.deploy();
		Json.installComponent("com.jfixby.cmns.adopted.gdx.json.RedJson");
		DB.installComponent("com.jfixby.cmns.db.mysql.MySQLDB");
		Sys.exit();
		final DBConfig config = DB.newDBConfig();

		config.setServerName(CONFIG.DB_SERVER);
		config.setLogin(CONFIG.DB_LOGIN);
		config.setPassword(CONFIG.DB_PASSWORD);
		config.setDBName(CONFIG.DB_NAME);
		config.setUseSSL(!true);
		config.setPort(3308);

		final DataBase mySQL = DB.newDB(config);

		final RedReporterDataBank bank = new RedReporterDataBank(mySQL);
		bank.resetTables();

	}

}
