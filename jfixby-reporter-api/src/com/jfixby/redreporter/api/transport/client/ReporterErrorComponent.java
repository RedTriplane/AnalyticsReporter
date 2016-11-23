
package com.jfixby.redreporter.api.transport.client;

import com.jfixby.cmns.api.err.ErrorComponent;

public interface ReporterErrorComponent extends ErrorComponent {

	void wrap (ErrorComponent err);

}
