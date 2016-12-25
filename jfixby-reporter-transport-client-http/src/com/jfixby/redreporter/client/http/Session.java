
package com.jfixby.redreporter.client.http;

public class Session {
	public Session () {
		this.sessionID = System.currentTimeMillis() + "";
	}

	String sessionID = "";

	public String getID () {
		return this.sessionID;
	}

}
