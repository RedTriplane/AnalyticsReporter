
package com.jfixby.redreporter.client.http;

import com.jfixby.cmns.api.net.message.Message;
import com.jfixby.redreporter.api.PROTOCOL;

public class Registrator {

	public static boolean registerDevice (final ServerHandlers servers, final HttpDeviceRegistration reg) {

		for (final ServerHandler server : servers) {
			final Message msg = new Message(PROTOCOL.REGISTER_DEVICE);
			final Message result = server.exchange(msg);
			if (result != null) {
				result.print();
				return true;
			}

		}
		return false;
	}

}
