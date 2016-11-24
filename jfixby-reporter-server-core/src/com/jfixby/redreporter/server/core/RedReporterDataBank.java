
package com.jfixby.redreporter.server.core;

import java.io.IOException;
import java.sql.SQLException;

import com.jfixby.cmns.db.mysql.MySQL;

public class RedReporterDataBank {

	private final MySQL mySQL;

	public RedReporterDataBank (final MySQL mySQL) {
		this.mySQL = mySQL;
	}

	{

// DB.installComponent(this.mySQL);

// final MySQLTable table = this.mySQL.getTable("installs");
//
// table.clear();
//
// final MySQLEntry entry = table.newMySQLEntry();
//
// final MySQLTableSchema schema = table.getSchema();
//
// entry.set(schema, 1, System.currentTimeMillis() + "");
// entry.set(schema, 2, "installID" + System.currentTimeMillis());
//
// table.addEntry(entry);
//
// final List<MySQLEntry> list = table.listAll();
// list.print("list");

	}

	public void connect () throws IOException {
		try {
			this.mySQL.connect();
		} catch (final SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
	}

	public void disconnect () {
		this.mySQL.disconnect();
	}

}
