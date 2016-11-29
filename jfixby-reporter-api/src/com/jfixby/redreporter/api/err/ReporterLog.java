
package com.jfixby.redreporter.api.err;

import com.jfixby.cmns.api.log.LoggerComponent;

public interface ReporterLog extends LoggerComponent {

	void wrap (LoggerComponent log);

}
