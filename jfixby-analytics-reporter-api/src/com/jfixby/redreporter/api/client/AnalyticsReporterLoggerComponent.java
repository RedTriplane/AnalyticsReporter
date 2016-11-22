
package com.jfixby.redreporter.api.client;

import com.jfixby.cmns.api.log.LoggerComponent;

public interface AnalyticsReporterLoggerComponent extends LoggerComponent {

	void wrap (LoggerComponent log);

}
