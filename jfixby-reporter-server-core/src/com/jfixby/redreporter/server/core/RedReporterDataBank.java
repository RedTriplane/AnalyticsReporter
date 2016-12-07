
package com.jfixby.redreporter.server.core;

import java.io.IOException;

import com.jfixby.cmns.api.collections.Collection;
import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.List;
import com.jfixby.cmns.api.collections.Map;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.util.path.RelativePath;
import com.jfixby.cmns.db.mysql.MySQL;
import com.jfixby.cmns.db.mysql.MySQLEntry;
import com.jfixby.cmns.db.mysql.MySQLTable;
import com.jfixby.cmns.db.mysql.MySQLTableSchema;
import com.jfixby.redreporter.server.api.ReportStoreArguments;

public class RedReporterDataBank {

	private final MySQL mySQL;

	public RedReporterDataBank (final MySQL mySQL) {
		this.mySQL = mySQL;
	}

	{

// DB.installComponent(this.mySQL);

	}

	public ServerSettings readSettings () throws IOException {
		final MySQLTable settingsTable;
		final ServerSettings result = new ServerSettings();

		settingsTable = this.mySQL.getTable(ServerSettings.TABLE_NAME);

		final List<MySQLEntry> settingslist = settingsTable.listAll();
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

	}

	public String registerInstallation (final String token) throws IOException {
		final MySQLTable table = this.mySQL.getTable(BankSchema.INSTALLS.TableName);

		final MySQLEntry entry = table.newMySQLEntry();

		final MySQLTableSchema schema = table.getSchema();

		entry.set(schema, schema.indexOf(BankSchema.INSTALLS.timestamp), System.currentTimeMillis() + "");
		entry.set(schema, schema.indexOf(BankSchema.INSTALLS.token), token + "");

		table.addEntry(entry);

		final String reg = token.toString();
		return reg;
	}

	public void updateSystemInfo (final String token, final Map<String, String> values) throws IOException {
		final Long install_id = this.findIDForToken(token.toString());
		if (install_id == null) {
			throw new IOException("Token not found " + token);
		}
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

	public Long findIDForToken (final String token) throws IOException {
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
			L.e("failed to read token", token);
			e.printStackTrace();
			throw new IOException(e);
		}
	}

	public void resetTables () throws IOException {
		{
			final MySQLTable table = this.mySQL.getTable(BankSchema.SYSTEM_INFO.TableName);

			table.clear();
		}
		{
			final MySQLTable table = this.mySQL.getTable(BankSchema.INSTALLS.TableName);
			table.clear();
		}
	}

	public void storeReport (final ReportStoreArguments store_args, final File logFile) throws IOException {
		final MySQLTable table = this.mySQL.getTable(BankSchema.SERIALIZED_REPORTS.TableName);
		final MySQLEntry entry = table.newMySQLEntry();
		final MySQLTableSchema schema = table.getSchema();

		final Long installID = store_args.getInstallID();
		final String fileID = store_args.getFileID();
		final Long received = store_args.getReceivedTimeStamp();
		final String written = store_args.getWrittenTimestamp();
		final String sent = store_args.getSentTimestamp();
		final String version = store_args.getVersionString();
		final RelativePath file_path = logFile.getAbsoluteFilePath().getRelativePath();

		entry.set(schema, schema.indexOf(BankSchema.SERIALIZED_REPORTS.install_id), installID);
		entry.set(schema, schema.indexOf(BankSchema.SERIALIZED_REPORTS.received_timestamp), received);
		entry.set(schema, schema.indexOf(BankSchema.SERIALIZED_REPORTS.written_timestamp), written);
		entry.set(schema, schema.indexOf(BankSchema.SERIALIZED_REPORTS.sent_timestamp), sent);
		entry.set(schema, schema.indexOf(BankSchema.SERIALIZED_REPORTS.report_version), version);
		entry.set(schema, schema.indexOf(BankSchema.SERIALIZED_REPORTS.file_id), fileID);
		entry.set(schema, schema.indexOf(BankSchema.SERIALIZED_REPORTS.file_path), file_path);

		table.addEntry(entry);

	}

}
