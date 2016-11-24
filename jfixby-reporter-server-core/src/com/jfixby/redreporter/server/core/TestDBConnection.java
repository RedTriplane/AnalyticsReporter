
package com.jfixby.redreporter.server.core;

import com.jfixby.cmns.adopted.gdx.json.RedJson;
import com.jfixby.cmns.api.file.LocalFileSystem;
import com.jfixby.cmns.api.json.Json;
import com.jfixby.cmns.db.api.DB;
import com.jfixby.cmns.db.api.TestConnectionResult;
import com.jfixby.cmns.db.mysql.MySQL;
import com.jfixby.cmns.db.mysql.MySQLConfig;
import com.jfixby.red.desktop.DesktopSetup;

public class TestDBConnection {

	public static void main (final String[] args) {

		DesktopSetup.deploy();
		Json.installComponent(new RedJson());

		LocalFileSystem.ApplicationHome().child("credentials");

		final MySQLConfig config = new MySQLConfig();

		DB.installComponent(new MySQL(config));

		final TestConnectionResult test = DB.testConnection();
		test.print();

	}

}
