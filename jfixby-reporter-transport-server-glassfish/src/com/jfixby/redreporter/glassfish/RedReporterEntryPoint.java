
package com.jfixby.redreporter.glassfish;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;

import com.jfixby.cmns.api.assets.ID;
import com.jfixby.cmns.api.assets.Names;
import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.List;
import com.jfixby.cmns.api.collections.Map;
import com.jfixby.cmns.api.io.Buffer;
import com.jfixby.cmns.api.io.BufferInputStream;
import com.jfixby.cmns.api.io.GZipInputStream;
import com.jfixby.cmns.api.io.IO;
import com.jfixby.cmns.api.io.InputStream;
import com.jfixby.cmns.api.io.OutputStream;
import com.jfixby.cmns.api.java.ByteArray;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.math.Average;
import com.jfixby.cmns.api.math.FloatMath;
import com.jfixby.cmns.api.math.IntegerMath;
import com.jfixby.cmns.api.net.message.Message;
import com.jfixby.cmns.api.sys.SystemInfoTags;
import com.jfixby.redreporter.api.InstallationID;
import com.jfixby.redreporter.api.transport.REPORTER_PROTOCOL;
import com.jfixby.redreporter.server.api.ReporterServer;

public class RedReporterEntryPoint extends AbstractEntryPoint {

	/**
	 *
	 */
	private static final long serialVersionUID = 8584178804364883918L;
	private static final long MAX_BYTES_TO_READ = 1024 * 100;
	static long request = 0;

	@Override
	void processRequest (final String session_id, final ServletInputStream client_to_server_stream,
		final Map<String, List<String>> inputHeaders, final ServletOutputStream server_to_client_stream,
		final Map<String, String> outputHeaders) {
		final RedReporterEntryPointArguments arg = new RedReporterEntryPointArguments();
		arg.timestamp = System.currentTimeMillis();
		arg.request_number = RedReporterEntryPoint.request_number();
		arg.inputHeaders = inputHeaders;
		arg.requestID = Names.ROOT().child("iid-" + instance_id).child("sid-" + session_id).child("rqn-" + arg.request_number);
		arg.server_to_client_stream = server_to_client_stream;

		arg.print();
		inputHeaders.print("inputHeaders");

		try {
			final String len = this.getHeader("content-length", inputHeaders);
			if (len == null) {
				this.sayHello(arg);
				return;
			}
			if ("0".equals(len)) {
				this.sayHello(arg);
				return;
			}
			final InputStream is = IO.newInputStream( () -> client_to_server_stream);
			is.open();

			final ByteArray bytes = IO.readMax(is, MAX_BYTES_TO_READ);
			final ByteArray data;
			{
				final Buffer buffer = IO.newBuffer(bytes);
				final BufferInputStream bis = IO.newBufferInputStream(buffer);
				bis.open();
				{
					final GZipInputStream gzip = IO.newGZipStream(bis);
					gzip.open();
					data = IO.readMax(gzip, MAX_BYTES_TO_READ);
					gzip.close();
				}
				bis.close();
			}

			arg.message = IO.deserialize(Message.class, data);
			is.close();

			final Message answer = this.processMessage(arg);
			if (answer == null) {
				return;
			}
			final OutputStream os = IO.newOutputStream( () -> server_to_client_stream);
			os.open();

			final ByteArray responceBytes = IO.serialize(answer);
			final ByteArray compressedResponse = IO.compress(responceBytes);
			os.write(compressedResponse);
			os.close();
		} catch (final Throwable e) {
			L.e(e);
			e.printStackTrace();
		}
		outputHeaders.print("outputHeaders");

		this.average.addValue(this.measureProcessingTime(arg));
	}

	private String getHeader (final String string, final Map<String, List<String>> inputHeaders) {
		final List<String> list = inputHeaders.get(string);
		if (list == null) {
			return null;
		}
		if (list.size() == 0) {
			return null;
		}
		return list.getElementAt(0);
	}

	private static synchronized long request_number () {
		request++;
		return request;
	}

	private Message processMessage (final RedReporterEntryPointArguments arg) throws IOException {
		if (REPORTER_PROTOCOL.REGISTER_INSTALLATION.equals(arg.message.header)) {
			return this.registerInstallation(arg);
		}
		return this.unknownHeader(arg);
	}

	private Message unknownHeader (final RedReporterEntryPointArguments arg) {
		L.d("unknown header");
		arg.message.print();
		return null;
	}

	public static final int MAX_PARAMETERS = 1000;

	private Message registerInstallation (final RedReporterEntryPointArguments arg) throws IOException {
		final Message result = new Message(REPORTER_PROTOCOL.INSTALLATION_TOKEN);

		final ID token = ReporterServer.invoke().newToken(arg.requestID);

		final InstallationID id = ReporterServer.registerInstallation(token);
		arg.message.values.put(SystemInfoTags.Net.client_ip, this.getHeader(SystemInfoTags.Net.client_ip, arg.inputHeaders));

		final Map<String, String> params = Collections.newMap();
		Collections.scanCollection(Collections.newList(arg.message.values.keySet()), 0,
			IntegerMath.min(MAX_PARAMETERS, arg.message.values.size()), (k, i) -> {
				params.put(k, arg.message.values.get(k));
			});

		ReporterServer.updateSystemInfo(token, params);

		L.d("register install", id.token);
		result.values.put(REPORTER_PROTOCOL.INSTALLATION_TOKEN, id.token);
		return result;
	}

	public final static String SEPARATOR = System.getProperty("line.separator");
	int MAX_VALUES = 10;
	final Average average = FloatMath.newAverage(this.MAX_VALUES);

	private void sayHello (final RedReporterEntryPointArguments arg) throws IOException {
		final StringBuilder msg = new StringBuilder();
		msg.append("Service is operating: " + serviceState()).append(SEPARATOR);
		this.average.addValue(this.measureProcessingTime(arg));
		final double sec = FloatMath.roundToDigit(this.average.getLast(), 3);
		msg.append("         server time: " + new Date()).append(SEPARATOR);
		msg.append("         	 version: " + version.getPackageVersionString()).append(SEPARATOR);
		msg.append(SEPARATOR);
		msg.append("           client ip: " + arg.inputHeaders.get(SystemInfoTags.Net.client_ip)).append(SEPARATOR);
		msg.append("          request id: " + arg.requestID).append(SEPARATOR);
		msg.append(SEPARATOR);
		msg.append("request processed in: " + sec + " sec").append(SEPARATOR);
		msg.append("average for the last: " + this.average.size() + " requests").append(SEPARATOR);
		msg.append("                  is: " + FloatMath.roundToDigit(this.average.getAverage(), 3) + " sec").append(SEPARATOR);

		arg.server_to_client_stream.write(msg.toString().getBytes());
	}

	private double measureProcessingTime (final RedReporterEntryPointArguments arg) {
		return (System.currentTimeMillis() - arg.timestamp) / 1000d;
	}

	public static final void main (final String[] arg) {

	}

}
