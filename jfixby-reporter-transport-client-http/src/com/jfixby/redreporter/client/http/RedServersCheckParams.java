
package com.jfixby.redreporter.client.http;

import com.jfixby.redreporter.api.transport.ServersCheckParams;

public class RedServersCheckParams implements ServersCheckParams {

	private long timeout = 15000;

	@Override
	public void setTimeOut (final long timeout) {
		this.timeout = timeout;
	}

	@Override
	public long getTimeOut () {
		return this.timeout;
	}

}
