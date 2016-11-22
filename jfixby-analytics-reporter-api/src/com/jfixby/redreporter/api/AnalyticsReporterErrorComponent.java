
package com.jfixby.redreporter.api;

import com.jfixby.cmns.api.err.ErrorComponent;

public interface AnalyticsReporterErrorComponent extends ErrorComponent {

	void wrap (ErrorComponent err);

}
