
package com.jfixby.redreporter.client.http;

import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.Map;
import com.jfixby.cmns.api.net.message.Message;
import com.jfixby.redreporter.api.PROTOCOL;

public class Registrator {

	public static boolean registerDevice (final ServerHandlers servers, final HttpDeviceRegistration reg) {

		for (final ServerHandler server : servers) {
			final Message msg = new Message(PROTOCOL.REGISTER_DEVICE);
			msg.values.put(PROTOCOL.RESPONSE_FORMAT, PROTOCOL.JSON);
			msg.values.put(PROTOCOL.REQUEST_FORMAT, PROTOCOL.GZIP);

			final Map<String, String> httpParams = Collections.newMap(msg.values);

			final Message result = server.exchange(msg, httpParams);
			if (result != null) {
				result.print();
				return true;
			}

		}
		return false;
	}

}
