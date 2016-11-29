
package com.jfixby.redreporter.server.core;

import java.io.IOException;
import java.sql.SQLException;

import com.jfixby.cmns.api.assets.ID;
import com.jfixby.cmns.api.collections.Collection;
import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.List;
import com.jfixby.cmns.api.collections.Map;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.db.mysql.MySQL;
import com.jfixby.cmns.db.mysql.MySQLEntry;
import com.jfixby.cmns.db.mysql.MySQLTable;
import com.jfixby.cmns.db.mysql.MySQLTableSchema;
import com.jfixby.redreporter.api.InstallationID;

public class RedReporterDataBank {

	private final MySQL mySQL;

	public RedReporterDataBank (final MySQL mySQL) {
		this.mySQL = mySQL;
	}

	{

// DB.installComponent(this.mySQL);

	}

// public void connect () throws IOException {
// try {
// this.mySQL.connect();
// } catch (final SQLException e) {
// e.printStackTrace();
// throw new IOException(e);
// }
// }
//
// public void disconnect () {
// this.mySQL.disconnect();
// }

	private void testReg () {
		try {
			final MySQLTable table = this.mySQL.getTable(BankSchema.INSTALLS.TableName);

			table.clear();

			final MySQLEntry entry = table.newMySQLEntry();

			final MySQLTableSchema schema = table.getSchema();
			schema.print();

			entry.set(schema, 2, System.currentTimeMillis() + "");
			entry.set(schema, 0, "installID" + System.currentTimeMillis());

			table.addEntry(entry);

			final List<MySQLEntry> list = table.listAll();
			list.print("list");
			this.mySQL.disconnect();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	MySQLTable settingsTable;

	public ServerSettings getServerSettings () throws IOException {
		try {
			this.mySQL.connect();
			final ServerSettings result = new ServerSettings();
			if (this.settingsTable == null) {
				this.settingsTable = this.mySQL.getTable(ServerSettings.TABLE_NAME);
			}
			final List<MySQLEntry> settingslist = this.settingsTable.listAll();
			final Map<String, String> settings = Collections.newMap();
			for (final MySQLEntry entry : settingslist) {
				final String parameter_name = entry.getValue(ServerSettings.PARAMETER_NAME);
				final String parameter_value = entry.getValue(ServerSettings.PARAMETER_VALUE);
				settings.put(parameter_name, parameter_value);
			}
			final String salt0 = settings.get(ServerSettings.SALT_0);
			result.setSalt0(salt0);
			if (salt0 == null) {
				throw new IOException("ServerSettings.salt0 is not found");
			}

			return result;
		} catch (final SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		} finally {
			this.mySQL.disconnect();
		}
	}

	public InstallationID registerInstallation (final ID token) throws IOException {
		try {
			this.mySQL.connect();
			this.registerToken(token);
			final InstallationID reg = new InstallationID();
			reg.token = token.toString();
			return reg;
		} catch (final SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		} finally {
			this.mySQL.disconnect();
		}
	}

	public void updateSystemInfo (final ID token, final Map<String, String> values) throws IOException {
		try {
			this.mySQL.connect();
			final Long id = this.getIDForToken(token.toString());
			if (id == null) {
				throw new SQLException("Token not found " + token);
			}
			this.registerSystemInfo(id, values);
		} catch (final SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		} finally {
			this.mySQL.disconnect();
		}
	}

	private Long getIDForToken (final String token) throws SQLException {
		final MySQLTable table = this.mySQL.getTable(BankSchema.INSTALLS.TableName);

		final Collection<MySQLEntry> list = table.findEntries(BankSchema.INSTALLS.token, token);
		if (list.size() == 0) {
			L.e("Token not found");
			return null;
		}

		final String idString = list.getLast().getValue(BankSchema.INSTALLS.id);
		try {
			final long result = Long.parseLong(idString);
			return result;
		} catch (final Throwable e) {
			list.print("sql result");
			L.e("failed to reak token", token);
			e.printStackTrace();
			throw new SQLException(e);
		}
	}

	private void registerToken (final ID token) throws SQLException {
		final MySQLTable table = this.mySQL.getTable(BankSchema.INSTALLS.TableName);

		final MySQLEntry entry = table.newMySQLEntry();

		final MySQLTableSchema schema = table.getSchema();

		entry.set(schema, schema.indexOf(BankSchema.INSTALLS.timestamp), System.currentTimeMillis() + "");
		entry.set(schema, schema.indexOf(BankSchema.INSTALLS.token), token + "");

		table.addEntry(entry);
	}

	private void registerSystemInfo (final long install_id, final Map<String, String> values) throws SQLException {
		final MySQLTable table = this.mySQL.getTable(BankSchema.SYSTEM_INFO.TableName);

		final MySQLTableSchema schema = table.getSchema();
		final List<MySQLEntry> batch = Collections.newList();
		for (final String key : values.keys()) {
			final MySQLEntry entry = table.newMySQLEntry();
			final String value = values.get(key);

			entry.set(schema, schema.indexOf(BankSchema.SYSTEM_INFO.install_id), install_id + "");
			entry.set(schema, schema.indexOf(BankSchema.SYSTEM_INFO.parameter_name), key);
			entry.set(schema, schema.indexOf(BankSchema.SYSTEM_INFO.parameter_value), value);

			batch.add(entry);
		}

		table.replaceEntries(batch);

	}

	public void resetTables () throws IOException {
		try {
			this.mySQL.connect();
			{
				final MySQLTable table = this.mySQL.getTable(BankSchema.SYSTEM_INFO.TableName);

				table.clear();
			}
			{
				final MySQLTable table = this.mySQL.getTable(BankSchema.INSTALLS.TableName);
				table.clear();
			}
		} catch (final Exception e) {
			throw new IOException(e);
		} finally {
			this.mySQL.disconnect();
		}
	}

}
