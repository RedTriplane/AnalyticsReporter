
package com.jfixby.redreporter.glassfish;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import com.jfixby.redreporter.api.ServerStatus;
import com.jfixby.redreporter.api.transport.REPORTER_PROTOCOL;
import com.jfixby.redreporter.api.transport.ReportData;
import com.jfixby.redreporter.server.api.DB_STATE;
import com.jfixby.redreporter.server.api.ReportFileStoreArguments;
import com.jfixby.redreporter.server.api.ReportRegistration;
import com.jfixby.redreporter.server.api.ReporterServer;
import com.jfixby.redreporter.server.api.STORAGE_STATE;
import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.List;
import com.jfixby.scarabei.api.collections.Map;
import com.jfixby.scarabei.api.io.IO;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.math.IntegerMath;
import com.jfixby.scarabei.api.net.message.Message;
import com.jfixby.scarabei.api.sys.SystemInfoTags;

public class MessageProcessor {

	public static final int MAX_PARAMETERS = 1000;

	static public Message process (final RedReporterEntryPointArguments arg) throws IOException {
		if (REPORTER_PROTOCOL.REGISTER_INSTALLATION.equals(arg.message.header)) {
			return registerInstallation(arg);
		}
		if (REPORTER_PROTOCOL.PING.equals(arg.message.header)) {
			final DB_STATE db = ReporterServer.getDBState();
			final STORAGE_STATE st = ReporterServer.getStorageState();
			arg.message.attachments.put(REPORTER_PROTOCOL.SERVER_STATUS, ServerStatus.OK);
			if (db != DB_STATE.OK) {
				arg.message.attachments.put(REPORTER_PROTOCOL.SERVER_STATUS, ServerStatus.ERROR);
			}
			if (st != STORAGE_STATE.OK) {
				arg.message.attachments.put(REPORTER_PROTOCOL.SERVER_STATUS, ServerStatus.ERROR);
			}

			return arg.message;
		}
		if (REPORTER_PROTOCOL.REPORT.equals(arg.message.header)) {
			return registerReport(arg);
		}
		return unknownHeader(arg);
	}

	private static Message registerReport (final RedReporterEntryPointArguments arg) {
		arg.receivedTimestamp = arg.timestamp;
		arg.sentTimestamp = arg.message.values.get(REPORTER_PROTOCOL.REPORT_SENT);
		arg.sessionID = arg.message.values.get(REPORTER_PROTOCOL.SESSION_ID);
		arg.writtenTimestamp = arg.message.values.get(REPORTER_PROTOCOL.REPORT_WRITTEN);
		arg.versionString = arg.message.values.get(REPORTER_PROTOCOL.REPORT_VERSION);
		arg.token = arg.message.values.get(REPORTER_PROTOCOL.INSTALLATION_TOKEN);
		arg.installID = ReporterServer.findInstallationID(arg.token);
		arg.resializedBody = (byte[])arg.message.attachments.get(REPORTER_PROTOCOL.REPORT);
		arg.subject = arg.message.values.get(REPORTER_PROTOCOL.SUBJECT);
		arg.author = arg.message.values.get(REPORTER_PROTOCOL.AUTHOR_ID);
		arg.writtenTimestamp = arg.message.values.get(REPORTER_PROTOCOL.REPORT_WRITTEN);

		if (arg.installID == null) {
			return new Message(REPORTER_PROTOCOL.INVALID_TOKEN);
		}
		if (arg.writtenTimestamp == null) {
			return new Message(REPORTER_PROTOCOL.IO_FAILED);
		}
		if (arg.subject == null) {
			return new Message(REPORTER_PROTOCOL.IO_FAILED);
		}
		if (arg.author == null) {
			return new Message(REPORTER_PROTOCOL.IO_FAILED);
		}

		if (arg.sentTimestamp == null) {
			return new Message(REPORTER_PROTOCOL.IO_FAILED);
		}
		if (arg.versionString == null) {
			return new Message(REPORTER_PROTOCOL.IO_FAILED);
		}
		if (arg.resializedBody == null) {
			return new Message(REPORTER_PROTOCOL.IO_FAILED);
		}

		final boolean success = saveReport(arg);
		if (!success) {
			return new Message(REPORTER_PROTOCOL.FAILED_TO_STORE_REPORT);
		}
		final Message result = new Message(REPORTER_PROTOCOL.REPORT_RECEIVED_OK);
		return result;

	}

	private static boolean saveReport (final RedReporterEntryPointArguments arg) {
		final byte[] reportData = arg.resializedBody;
		final ReportData report;
		try {
			report = IO.deserialize(ReportData.class, reportData);
		} catch (final Throwable e) {
			ReporterServer.reportDeserializationtionProblem(e);

			final String storeBadInFile = System.getenv("STORE_BAD_REPORT_IN_FILE");
			if (!"true".equalsIgnoreCase(storeBadInFile)) {
				return false;
			}
			return saveBadReport(arg);
		}

		final ReportRegistration reg = ReporterServer.newReportRegistration();
		packReport(report, reg, arg);
		final boolean success = ReporterServer.registerReport(reg);
		return success;
	}

	private static void packReport (final ReportData report, final ReportRegistration reg,
		final RedReporterEntryPointArguments arg) {

		{
			final String reportID = arg.requestID.toString();
			reg.setReportUID(reportID);
			reg.setReceivedTimeStamp(arg.receivedTimestamp);
			reg.setInstallID(arg.installID);
			reg.setWrittenTimestamp(arg.writtenTimestamp);
			reg.setSentTimestamp(arg.sentTimestamp);
			reg.setSessionID(arg.sessionID);
			reg.setVersionString(arg.versionString);
			reg.setAuthor(arg.author);
			reg.setSubject(arg.subject);

		}

		{
			final LinkedHashMap<String, ArrayList<HashMap<String, String>>> collection = report.strings;
			final Set<String> stringKeys = collection.keySet();

			for (final String key : stringKeys) {
				final ArrayList<HashMap<String, String>> values = collection.get(key);
				packValues(key, values, reg);
			}
		}

		{
			final LinkedHashMap<String, ArrayList<HashMap<String, String>>> collection = report.exceptions;
			final Set<String> stringKeys = collection.keySet();

			for (final String key : stringKeys) {
				final ArrayList<HashMap<String, String>> values = collection.get(key);
				packExceptions(key, values, reg);
			}
		}

	}

	private static void packValues (final String key, final ArrayList<HashMap<String, String>> values,
		final ReportRegistration reg) {
		for (final HashMap<String, String> stat : values) {
			final String name = key;
			final String value = stat.get(ReportData.PARAMETER_VALUE);
			final String timeStamp = stat.get(ReportData.PARAMETER_TIMESTAMP);
			reg.addParameter(name, value, timeStamp);
		}
	}

	private static void packExceptions (final String key, final ArrayList<HashMap<String, String>> values,
		final ReportRegistration reg) {
		for (final HashMap<String, String> stat : values) {
			final String name = key;
			final String value = stat.get(ReportData.PARAMETER_VALUE);
			final String timeStamp = stat.get(ReportData.PARAMETER_TIMESTAMP);
			reg.addException(name, value, timeStamp);
		}
	}

	private static boolean saveBadReport (final RedReporterEntryPointArguments arg) {
		final ReportFileStoreArguments store_args = ReporterServer.newReportFileStoreArguments();
		final String reportID = arg.requestID.toString();
		store_args.setReportUID(reportID);
		store_args.setAuthor(arg.author);
		store_args.setSubject(arg.subject);
		store_args.setReceivedTimeStamp(arg.receivedTimestamp);
		store_args.setReceivedTimeStamp(arg.receivedTimestamp);
		store_args.setInstallID(arg.installID);
		store_args.setWrittenTimestamp(arg.writtenTimestamp);
		store_args.setSentTimestamp(arg.sentTimestamp);
		store_args.setSessionID(arg.sessionID);
		store_args.setVersionString(arg.versionString);

		final String fileName = arg.requestID.child("log") + "";
		store_args.setFileID(fileName);
		store_args.setReportData(arg.resializedBody);
		final boolean success = ReporterServer.storeReportFile(store_args);
		return success;
	}

	static private Message unknownHeader (final RedReporterEntryPointArguments arg) {
		L.d("unknown header");
		arg.message.print();
		return new Message(REPORTER_PROTOCOL.UNKNOWN_HEADER);
	}

	static private Message registerInstallation (final RedReporterEntryPointArguments arg) {
		final Message result = new Message(REPORTER_PROTOCOL.INSTALLATION_TOKEN);

		final String token = ReporterServer.newToken(arg.requestID);

		if (token == null) {
			return new Message(REPORTER_PROTOCOL.IO_FAILED);
		}

		final String token_string = ReporterServer.registerInstallation(token);

		if (token_string == null) {
			return new Message(REPORTER_PROTOCOL.IO_FAILED);
		}

		arg.message.values.put(SystemInfoTags.Net.client_ip, getHeader(SystemInfoTags.Net.client_ip, arg.inputHeaders));

		final boolean success = ReporterServer.updateSystemInfo(token_string, params(arg.message.values));

		if (!success) {
			return new Message(REPORTER_PROTOCOL.IO_FAILED);
		}

		L.d("register install", token_string);
		result.values.put(REPORTER_PROTOCOL.INSTALLATION_TOKEN, token_string);
		return result;
	}

	private static Map<String, String> params (final LinkedHashMap<String, String> values) {

		final Map<String, String> params = Collections.newMap();
		Collections.scanCollection(Collections.newList(values.keySet()), 0, IntegerMath.min(MAX_PARAMETERS, values.size()),
			(k, i) -> {
				params.put(k, values.get(k));
			});

		return params;
	}

	static public String getHeader (final String string, final Map<String, List<String>> inputHeaders) {
		final List<String> list = inputHeaders.get(string);
		if (list == null) {
			return null;
		}
		if (list.size() == 0) {
			return null;
		}
		return list.getElementAt(0);
	}

}
