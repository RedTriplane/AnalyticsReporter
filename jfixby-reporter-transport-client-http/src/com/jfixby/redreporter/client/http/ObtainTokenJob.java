
package com.jfixby.redreporter.client.http;

import com.jfixby.cmns.api.taskman.Job;

public class ObtainTokenJob implements Job {

	private final ReportsQueue reportsQueue;
	private final ReporterHttpClient transport;

	public ObtainTokenJob (final ReportsQueue reportsQueue) {
		this.reportsQueue = reportsQueue;
		this.transport = reportsQueue.getTransport();
	}

	@Override
	public void doStart () throws Throwable {
	}

	@Override
	public void doPush () throws Throwable {
		if (this.isDone()) {
			return;
		}
		final boolean success = this.transport.registerInstallation();
	}

	@Override
	public boolean isDone () {
		final String token = this.transport.getInstallationID();
		return (!(token == null || "".equals(token)));
	}

}
