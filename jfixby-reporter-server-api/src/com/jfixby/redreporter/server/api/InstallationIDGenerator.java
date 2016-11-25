
package com.jfixby.redreporter.server.api;

import com.jfixby.cmns.api.assets.ID;

public interface InstallationIDGenerator {

	public ID newInstallationID (final String... args);

	public String applySalt (String input);

}
