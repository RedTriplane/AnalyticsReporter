
package com.jfixby.redreporter.api;

public class InstallationID implements java.io.Serializable {

	private static final long serialVersionUID = 7589968867676839460L;

	public InstallationID (final String token) {
		this.token = token;

	}

	final public String token;

	@Override
	public String toString () {
		return this.token + " (" + this.token.length() + ")";
	}

}
