
package com.jfixby.redreporter.client;

import com.jfixby.cmns.api.err.ErrorComponent;
import com.jfixby.redreporter.api.AnalyticsReporterErrorComponent;

public class RedAnalyticsReporterErrorComponent implements AnalyticsReporterErrorComponent {

	private final AbstractClient master;

	public RedAnalyticsReporterErrorComponent (final AbstractClient abstractClient) {
		this.master = abstractClient;
	}

	@Override
	public void reportWarning (final String message) {
	}

	@Override
	public void reportError (final String message) {
	}

	@Override
	public void reportError (final Throwable e) {
	}

	@Override
	public void reportError (final String message, final Throwable e) {
	}

	@Override
	public void reportNotImplementedYet () {
	}

	@Override
	public void wrap (final ErrorComponent err) {
	}

}
