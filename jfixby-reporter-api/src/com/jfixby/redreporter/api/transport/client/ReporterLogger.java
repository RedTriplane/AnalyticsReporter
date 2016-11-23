
package com.jfixby.redreporter.api.transport.client;

import com.jfixby.cmns.api.log.LoggerComponent;

public interface ReporterLogger extends LoggerComponent {

	void wrap (LoggerComponent log);

}
