
package com.jfixby.redreporter.server.core.run;

import java.io.IOException;

import com.jfixby.cmns.adopted.gdx.json.RedJson;
import com.jfixby.cmns.api.collections.Collection;
import com.jfixby.cmns.api.json.Json;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.db.mysql.MySQL;
import com.jfixby.cmns.db.mysql.MySQLConfig;
import com.jfixby.cmns.db.mysql.MySQLEntry;
import com.jfixby.cmns.db.mysql.MySQLTable;
import com.jfixby.cmns.db.mysql.MySQLTableSchema;
import com.jfixby.red.desktop.DesktopSetup;
import com.jfixby.redreporter.server.core.ServerSettings;
import com.jfixby.redreporter.server.credentials.CONFIG;

public class UploadServerSettings {

	public static void main (final String[] args) throws IOException {
		DesktopSetup.deploy();
		Json.installComponent(new RedJson());

		final MySQLConfig config = new MySQLConfig();

		config.setServerName(CONFIG.DB_SERVER);
		config.setLogin(CONFIG.DB_LOGIN);
		config.setPassword(CONFIG.DB_PASSWORD);
		config.setDBName(CONFIG.DB_NAME);
		config.setUseSSL(!true);

		final MySQL mySQL = new MySQL(config);

		final MySQLTable settingsTable = mySQL.getTable(ServerSettings.TABLE_NAME);

		final Collection<MySQLEntry> salt0 = settingsTable.findEntries(ServerSettings.PARAMETER_NAME, ServerSettings.SALT_0);
		salt0.print("salt0");
		if (salt0.size() == 0) {

			final MySQLTableSchema schema = settingsTable.getSchema();
			final int indexOfParamaterName = schema.getColumns().indexOf(ServerSettings.PARAMETER_NAME);
			final int indexOfParamaterValue = schema.getColumns().indexOf(ServerSettings.PARAMETER_VALUE);
			final MySQLEntry salt = settingsTable.newMySQLEntry();
			salt.set(schema, indexOfParamaterName, ServerSettings.SALT_0);
			salt.set(schema, indexOfParamaterValue, CONFIG.SALT_0);
			L.d("writing", salt);

			settingsTable.addEntry(salt);
		}

		settingsTable.listAll().print("ServerSettings");
	}

}
