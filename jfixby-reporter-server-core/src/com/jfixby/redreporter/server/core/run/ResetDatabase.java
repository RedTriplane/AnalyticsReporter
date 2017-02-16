
package com.jfixby.redreporter.server.core.run;

import java.io.IOException;
import java.sql.SQLException;

import com.jfixby.redreporter.server.core.RedReporterDataBank;
import com.jfixby.redreporter.server.credentials.CONFIG;
import com.jfixby.scarabei.adopted.gdx.json.GoogleGson;
import com.jfixby.scarabei.api.desktop.ScarabeiDesktop;
import com.jfixby.scarabei.api.json.Json;
import com.jfixby.scarabei.api.sys.Sys;
import com.jfixby.scarabei.db.api.DB;
import com.jfixby.scarabei.db.api.DBConfig;
import com.jfixby.scarabei.db.api.DataBase;
import com.jfixby.scarabei.db.mysql.MySQLDB;

public class ResetDatabase {

	public static void main (final String[] args) throws IOException, SQLException {
		ScarabeiDesktop.deploy();
		Json.installComponent(new GoogleGson());
		DB.installComponent(new MySQLDB());
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
