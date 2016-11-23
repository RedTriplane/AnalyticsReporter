
package com.jfixby.redreporter.glassfish;

import java.util.HashMap;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;

import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.Map;
import com.jfixby.cmns.api.io.IO;
import com.jfixby.cmns.api.io.InputStream;
import com.jfixby.cmns.api.io.OutputStream;
import com.jfixby.cmns.api.java.ByteArray;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.net.message.Message;

public class TestProcessor extends AbstractEntryPoint {

	/**
	 *
	 */
	private static final long serialVersionUID = 8584178804364883918L;
	static long request = 0;

	@Override
	void processRequest (final String session_id, final ServletInputStream client_to_server_stream,

		final HashMap<String, String> client_to_server_headers, final ServletOutputStream server_to_client_stream,
		final HashMap<String, String> server_to_client_headers) {

		L.d("---[" + (++request) + "]-----------------------------------");

		L.d("session_id", session_id);
		final Map<String, String> inputHeaders = Collections.newMap(client_to_server_headers);
		inputHeaders.print("inputHeaders");

		final Map<String, String> outputHeaders = Collections.newMap(server_to_client_headers);
		outputHeaders.print("outputHeaders");

		try {
			final String len = inputHeaders.get("content-length");
			if (len == null) {
				return;
			}
			if ("0".equals(len)) {
				return;
			}
			final InputStream is = IO.newInputStream( () -> client_to_server_stream);
			is.open();
			final ByteArray bytes = is.readAll();
			if (bytes == null) {
				return;
			}
			final ByteArray data = IO.decompress(bytes);
			if (data == null) {
				return;
			}
			final Message message = IO.deserialize(Message.class, data);
			is.close();
			message.print();

			final OutputStream os = IO.newOutputStream( () -> server_to_client_stream);
			os.open();

			final ByteArray responceBytes = IO.serialize(message);
			final ByteArray compressedResponse = IO.compress(responceBytes);
			os.write(compressedResponse);
			os.close();
		} catch (final Exception e) {
			L.e(e);
		}

	}

	public static final void main (final String[] arg) {

	}

}
