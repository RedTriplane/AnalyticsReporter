
package com.jfixby.redreporter.server.core;

import java.io.IOException;
import java.sql.SQLException;

import com.jfixby.cmns.api.collections.List;
import com.jfixby.cmns.db.mysql.MySQL;
import com.jfixby.cmns.db.mysql.MySQLEntry;
import com.jfixby.cmns.db.mysql.MySQLTable;
import com.jfixby.cmns.db.mysql.MySQLTableSchema;

public class RedReporterDataBank {

	private final MySQL mySQL;

	public RedReporterDataBank (final MySQL mySQL) {
		this.mySQL = mySQL;
	}

	{

// DB.installComponent(this.mySQL);

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

	public void testReg () {
		try {
			final MySQLTable table = this.mySQL.getTable("installs");

			table.clear();

			final MySQLEntry entry = table.newMySQLEntry();

			final MySQLTableSchema schema = table.getSchema();
			schema.print();

			entry.set(schema, 2, System.currentTimeMillis() + "");
			entry.set(schema, 0, "installID" + System.currentTimeMillis());

			table.addEntry(entry);

			final List<MySQLEntry> list = table.listAll();
			list.print("list");
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

}
