
package com.jfixby.redreporter.glassfish;

import java.util.HashMap;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;

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

public class TestProcessor extends AbstractEntryPoint {

	/**
	 *
	 */
	private static final long serialVersionUID = 8584178804364883918L;
	private static final long MAX_BYTES_TO_READ = 1024 * 1024;
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
			message.print();

			final OutputStream os = IO.newOutputStream( () -> server_to_client_stream);
			os.open();

			final ByteArray responceBytes = IO.serialize(message);
			final ByteArray compressedResponse = IO.compress(responceBytes);
			os.write(compressedResponse);
			os.close();
		} catch (final Throwable e) {
			L.e(e);
		}

	}

	public static final void main (final String[] arg) {

	}

}
