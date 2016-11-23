
package com.jfixby.redreporter.client.http;

import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.Map;
import com.jfixby.cmns.api.net.message.Message;
import com.jfixby.redreporter.api.ANALYTICS_REPORTER_PROTOCOL;

public class Registrator {

	public static boolean registerDevice (final ServerHandlers servers, final HttpDeviceRegistration reg) {

		for (final ServerHandler server : servers) {
			final Message msg = new Message(ANALYTICS_REPORTER_PROTOCOL.REGISTER_DEVICE);
			msg.values.put(ANALYTICS_REPORTER_PROTOCOL.REQUEST_FORMAT, ANALYTICS_REPORTER_PROTOCOL.JSON);
			msg.values.put(ANALYTICS_REPORTER_PROTOCOL.RESPONSE_FORMAT, ANALYTICS_REPORTER_PROTOCOL.JSON);

			final Map<String, String> httpParams = Collections.newMap(msg.values);
// httpParams.clear();
			httpParams.print("httpParams");

			final Message result = server.exchange(msg, httpParams);
			if (result != null) {
				result.print();
				return true;
			}

		}
		return false;
	}

}
