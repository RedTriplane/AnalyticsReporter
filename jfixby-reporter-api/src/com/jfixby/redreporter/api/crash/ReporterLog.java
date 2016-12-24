
package com.jfixby.redreporter.api.crash;

import com.jfixby.scarabei.api.log.LoggerComponent;

public interface ReporterLog extends LoggerComponent {

	void wrap (LoggerComponent log);

}
