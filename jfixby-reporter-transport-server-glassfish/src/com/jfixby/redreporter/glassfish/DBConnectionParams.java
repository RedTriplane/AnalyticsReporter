
package com.jfixby.redreporter.glassfish;

import com.jfixby.redreporter.server.credentials.CONFIG;
import com.jfixby.scarabei.db.api.ConnectionParametersProvider;

public class DBConnectionParams implements ConnectionParametersProvider {
	@Override
	public String getHost () {
		final String host = System.getenv("RDS_HOSTNAME");
		if (host == null) {
			return CONFIG.DB_SERVER;
		}
		return host;
	}

	@Override
	public int getPort () {
		final String port = System.getenv("RDS_PORT");
		if (port == null) {
			return 3306;
		}
		if ("".equals(port)) {
			return -1;
		}
		return Integer.parseInt(port);
	}

	@Override
	public String getLogin () {
		final String login = System.getenv("RDS_USERNAME");
		if (login == null) {
			return CONFIG.DB_LOGIN;
		}
		return login;
	}

	@Override
	public String getPassword () {
		final String pwd = System.getenv("RDS_PASSWORD");
		if (pwd == null) {
			return CONFIG.DB_PASSWORD;
		}
		return pwd;
	}

	@Override
	public String getDBName () {
		final String dbName = System.getenv("RDS_DB_NAME");
		if (dbName == null) {
			return CONFIG.DB_NAME;
		}
		return dbName;
	}

};
