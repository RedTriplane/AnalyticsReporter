
package com.jfixby.redreporter.api.err;

import com.jfixby.cmns.api.err.ErrorComponent;

public interface ReporterErr extends ErrorComponent {

	void wrap (ErrorComponent err);

}
