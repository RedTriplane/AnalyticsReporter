
package com.jfixby.redreporter.client;

import com.jfixby.cmns.api.log.LoggerComponent;
import com.jfixby.redreporter.api.AnalyticsReporterLoggerComponent;

public class RedAnalyticsReporterLoggerComponent implements AnalyticsReporterLoggerComponent {

	private final AbstractClient master;

	public RedAnalyticsReporterLoggerComponent (final AbstractClient abstractClient) {
		this.master = abstractClient;
	}

	@Override
	public void d (final Object msg) {
	}

	@Override
	public void d (final Object... msg) {
	}

	@Override
	public void d (final Object tag, final Object msg) {
	}

	@Override
	public void e (final Object msg) {
	}

	@Override
	public void e (final Object tag, final Object msg) {
	}

	@Override
	public void d () {
	}

	@Override
	public void e () {
	}

	@Override
	public void d_addChars (final Object msg) {
	}

	@Override
	public String toString (final Object[] array) {
		return null;
	}

	@Override
	public void wrap (final LoggerComponent log) {
	}

}
