
package com.jfixby.redreporter.glassfish;

import java.io.IOException;
import java.util.LinkedHashMap;

import com.jfixby.cmns.api.assets.ID;
import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.List;
import com.jfixby.cmns.api.collections.Map;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.math.IntegerMath;
import com.jfixby.cmns.api.net.message.Message;
import com.jfixby.cmns.api.sys.SystemInfoTags;
import com.jfixby.redreporter.api.transport.REPORTER_PROTOCOL;
import com.jfixby.redreporter.server.api.ReporterServer;

public class MessageProcessor {

	public static final int MAX_PARAMETERS = 1000;

	static public Message process (final RedReporterEntryPointArguments arg) throws IOException {
		if (REPORTER_PROTOCOL.REGISTER_INSTALLATION.equals(arg.message.header)) {
			return registerInstallation(arg);
		}
		if (REPORTER_PROTOCOL.PING.equals(arg.message.header)) {
			arg.message.attachments.put(REPORTER_PROTOCOL.SERVER_STATUS, ReporterServer.getStatus());
			return arg.message;
		}
		if (REPORTER_PROTOCOL.REPORT.equals(arg.message.header)) {
			return registerReport(arg);
		}
		return unknownHeader(arg);
	}

	private static Message registerReport (final RedReporterEntryPointArguments arg) {
		final String token = arg.message.values.get(REPORTER_PROTOCOL.INSTALLATION_TOKEN);
		final String sentTimestamp = arg.message.values.get(REPORTER_PROTOCOL.REPORT_SENT);
		final String writtenTimestamp = arg.message.values.get(REPORTER_PROTOCOL.REPORT_WRITTEN);
		final String versionString = arg.message.values.get(REPORTER_PROTOCOL.REPORT_VERSION);
		final byte[] resializedBody = (byte[])arg.message.attachments.get(REPORTER_PROTOCOL.REPORT);
		final Message result = new Message(REPORTER_PROTOCOL.REPORT_RECEIVED_OK);

// ReporterServer.re

		return result;
	}

	static private Message unknownHeader (final RedReporterEntryPointArguments arg) {
		L.d("unknown header");
		arg.message.print();
		return new Message(REPORTER_PROTOCOL.UNKNOWN_HEADER);
	}

	static private Message registerInstallation (final RedReporterEntryPointArguments arg) {
		final Message result = new Message(REPORTER_PROTOCOL.INSTALLATION_TOKEN);

		final ID token = ReporterServer.newToken(arg.requestID);

		if (token == null) {
			return new Message(REPORTER_PROTOCOL.IO_FAILED);
		}

		final String id = ReporterServer.registerInstallation(token);

		if (id == null) {
			return new Message(REPORTER_PROTOCOL.IO_FAILED);
		}

		arg.message.values.put(SystemInfoTags.Net.client_ip, getHeader(SystemInfoTags.Net.client_ip, arg.inputHeaders));

		final boolean success = ReporterServer.updateSystemInfo(token, params(arg.message.values));

		if (!success) {
			return new Message(REPORTER_PROTOCOL.IO_FAILED);
		}

		L.d("register install", id);
		result.values.put(REPORTER_PROTOCOL.INSTALLATION_TOKEN, id);
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
