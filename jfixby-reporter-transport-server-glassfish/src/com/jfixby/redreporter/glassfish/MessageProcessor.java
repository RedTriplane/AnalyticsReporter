
package com.jfixby.redreporter.glassfish;

import java.io.IOException;
import java.util.LinkedHashMap;

import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.List;
import com.jfixby.cmns.api.collections.Map;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.math.IntegerMath;
import com.jfixby.cmns.api.net.message.Message;
import com.jfixby.cmns.api.sys.SystemInfoTags;
import com.jfixby.redreporter.api.ServerStatus;
import com.jfixby.redreporter.api.transport.REPORTER_PROTOCOL;
import com.jfixby.redreporter.server.api.DB_STATE;
import com.jfixby.redreporter.server.api.ReportStoreArguments;
import com.jfixby.redreporter.server.api.ReporterServer;
import com.jfixby.redreporter.server.api.STORAGE_STATE;

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
		final Long receivedTimestamp = arg.timestamp;

		final String sentTimestamp = arg.message.values.get(REPORTER_PROTOCOL.REPORT_SENT);
		final String writtenTimestamp = arg.message.values.get(REPORTER_PROTOCOL.REPORT_WRITTEN);
		final String versionString = arg.message.values.get(REPORTER_PROTOCOL.REPORT_VERSION);

		final String token = arg.message.values.get(REPORTER_PROTOCOL.INSTALLATION_TOKEN);

		final Long installID = ReporterServer.findInstallationID(token);

		final ReportStoreArguments store_args = ReporterServer.newReportStoreArguments();

		store_args.setReceivedTimeStamp(receivedTimestamp);

		if (installID == null) {
			return new Message(REPORTER_PROTOCOL.INVALID_TOKEN);
		}
		store_args.setInstallID(installID);

		if (writtenTimestamp == null) {
			return new Message(REPORTER_PROTOCOL.IO_FAILED);
		}
		store_args.setWrittenTimestamp(writtenTimestamp);

		if (sentTimestamp == null) {
			return new Message(REPORTER_PROTOCOL.IO_FAILED);
		}
		store_args.setSentTimestamp(sentTimestamp);

		if (versionString == null) {
			return new Message(REPORTER_PROTOCOL.IO_FAILED);
		}
		store_args.setVersionString(versionString);

		final String fileName = arg.requestID.child("log") + "";
		store_args.setFileID(fileName);

		final byte[] resializedBody = (byte[])arg.message.attachments.get(REPORTER_PROTOCOL.REPORT);
		if (resializedBody == null) {
			return new Message(REPORTER_PROTOCOL.IO_FAILED);
		}
		store_args.setReportData(resializedBody);

		final boolean success = ReporterServer.storeReport(store_args);
		if (!success) {
			return new Message(REPORTER_PROTOCOL.FAILED_TO_STORE_REPORT);
		}

		final Message result = new Message(REPORTER_PROTOCOL.REPORT_RECEIVED_OK);

		return result;
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
