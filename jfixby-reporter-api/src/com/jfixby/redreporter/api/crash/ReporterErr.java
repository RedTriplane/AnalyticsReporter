
package com.jfixby.redreporter.api.crash;

import com.jfixby.scarabei.api.err.ErrorComponent;

public interface ReporterErr extends ErrorComponent {

	void wrap (ErrorComponent err);

}
