
package com.jfixby.redreporter.glassfish;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;

import com.jfixby.cmns.api.assets.ID;
import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.Map;
import com.jfixby.cmns.api.io.Buffer;
import com.jfixby.cmns.api.io.BufferInputStream;
import com.jfixby.cmns.api.io.GZipInputStream;
import com.jfixby.cmns.api.io.IO;
import com.jfixby.cmns.api.io.InputStream;
import com.jfixby.cmns.api.io.OutputStream;
import com.jfixby.cmns.api.java.ByteArray;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.net.message.Message;
import com.jfixby.redreporter.api.InstallationID;
import com.jfixby.redreporter.api.transport.REPORTER_PROTOCOL;
import com.jfixby.redreporter.server.api.ReporterServer;

public class RedReporterEntryPoint extends AbstractEntryPoint {

	/**
	 *
	 */
	private static final long serialVersionUID = 8584178804364883918L;
	private static final long MAX_BYTES_TO_READ = 1024 * 100;
	static long request_number = 0;
	private String session_id;

	@Override
	void processRequest (final String session_id, final ServletInputStream client_to_server_stream,

		final HashMap<String, String> client_to_server_headers, final ServletOutputStream server_to_client_stream,
		final HashMap<String, String> server_to_client_headers) {

		L.d("---[" + (request_number++) + "]-----------------------------------");

		L.d("session_id", session_id);
		this.session_id = session_id;
		L.d("instance_id", ReporterServer.getInstanceID());
		final Map<String, String> inputHeaders = Collections.newMap(client_to_server_headers);
		inputHeaders.print("inputHeaders");

		final Map<String, String> outputHeaders = Collections.newMap(server_to_client_headers);
		outputHeaders.print("outputHeaders");

		try {
			final String len = inputHeaders.get("content-length");
			if (len == null) {
				this.sayHello(server_to_client_stream);
				return;
			}
			if ("0".equals(len)) {
				this.sayHello(server_to_client_stream);
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

			final Message message = IO.deserialize(Message.class, data);
			is.close();

			final Message answer = this.processMessage(message);
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

	}

	private Message processMessage (final Message message) throws IOException {

		if (REPORTER_PROTOCOL.REGISTER_INSTALLATION.equals(message.header)) {
			return this.registerInstallation(message);
		}

		return this.unknownHeader(message);
	}

	private Message unknownHeader (final Message message) {
		L.d("unknown header");
		message.print();
// final Message result = new Message(REPORTER_PROTOCOL.UNKNOWN_HEADER);
		return null;
	}

	private Message registerInstallation (final Message request) throws IOException {
		final Message result = new Message(REPORTER_PROTOCOL.INSTALLATION_TOKEN);
// request.print();
// request.values.put("instance_id", ReporterServer.getInstanceID());
// request.values.put("session_id", this.session_id);

		final ID token = ReporterServer.invoke().newToken(this.session_id, "" + request_number);

		final InstallationID id = ReporterServer.registerInstallation(token);
		ReporterServer.updateSystemInfo(token, request.values);

		L.d("register install", id.token);
		result.values.put(REPORTER_PROTOCOL.INSTALLATION_TOKEN, id.token);

		return result;
	}

	private void sayHello (final ServletOutputStream server_to_client_stream) throws IOException {
		server_to_client_stream.write(("Service is operating normally " + new Date()).getBytes());
	}

	public static final void main (final String[] arg) {

	}

}
