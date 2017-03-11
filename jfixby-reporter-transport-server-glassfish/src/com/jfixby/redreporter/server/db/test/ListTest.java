
package com.jfixby.redreporter.server.db.test;

import java.io.IOException;

import com.jfixby.redreporter.server.core.BankSchema;
import com.jfixby.redreporter.server.credentials.CONFIG;
import com.jfixby.scarabei.api.collections.Collection;
import com.jfixby.scarabei.api.desktop.ScarabeiDesktop;
import com.jfixby.scarabei.api.json.Json;
import com.jfixby.scarabei.db.api.DB;
import com.jfixby.scarabei.db.api.DBConfig;
import com.jfixby.scarabei.db.api.DataBase;
import com.jfixby.scarabei.db.api.Entry;
import com.jfixby.scarabei.db.api.Table;
import com.jfixby.scarabei.db.api.TableSchema;
import com.jfixby.scarabei.db.mysql.MySQLDB;
import com.jfixby.scarabei.gson.GoogleGson;

public class ListTest {
	public static void main (final String[] args) throws IOException {

		ScarabeiDesktop.deploy();
		Json.installComponent(new GoogleGson());
		DB.installComponent(new MySQLDB());

		final DBConfig config = DB.newDBConfig();

		config.setServerName(CONFIG.DB_SERVER);
		config.setLogin(CONFIG.DB_LOGIN);
		config.setPassword(CONFIG.DB_PASSWORD);
		config.setDBName(CONFIG.DB_NAME);
		config.setUseSSL(!true);

		final DataBase mySQL = DB.newDB(config);

		final Table table = mySQL.getTable(BankSchema.SYSTEM_INFO.TableName);
		final TableSchema schema = table.getSchema();
		final int indexOf = schema.indexOf(BankSchema.SYSTEM_INFO.install_id);
		final Collection<Entry> entries = table.findEntries(schema, indexOf, "1");
		entries.print("find");

		final Entry toDelete = entries.getElementAt(0);
		table.deleteEntry(toDelete);

	}
}
