
package com.jfixby.redreporter.server.core.run;

import com.jfixby.cmns.adopted.gdx.json.RedJson;
import com.jfixby.cmns.api.json.Json;
import com.jfixby.cmns.db.api.DB;
import com.jfixby.cmns.db.mysql.MySQL;
import com.jfixby.cmns.db.mysql.MySQLConfig;
import com.jfixby.red.desktop.DesktopSetup;
import com.jfixby.redreporter.server.credentials.CONFIG;

public class TestDBConnection {

	public static void main (final String[] args) {

		DesktopSetup.deploy();
		Json.installComponent(new RedJson());

		final MySQLConfig config = new MySQLConfig();

		config.setDBUrlString(CONFIG.DB_URL_STRING);
		config.setLogin(CONFIG.DB_LOGIN);
		config.setPassword(CONFIG.DB_PASSWORD);

		DB.installComponent(new MySQL(config));

	}

}
