
package com.jfixby.redreporter.client.http;

import com.jfixby.redreporter.api.ServerStatus;
import com.jfixby.scarabei.api.net.http.HttpURL;

public class ServerPing {

	public static final String NO_RESPONSE = "NO_RESPONSE";
	public static final String UNKNOWN = "UNKNOWN";

	public long ping = Long.MAX_VALUE;
	public int code = -1;
	public ServerStatus status = ServerStatus.NO_RESPONSE;
	public String serverVersion = UNKNOWN;
	public long serverProcesingTime;
	public HttpURL url;
	public ServerHandler server;
	public String error;
	public String request_id = "";

	public boolean isGood () {
		if (this.code == -1) {
			return false;
		}
		if (this.status != ServerStatus.OK) {
			return false;
		}
		if (this.error != null) {
			return false;
		}
		return true;
	}

	@Override
	public String toString () {
		if (this.error == null) {
			return "[" + this.code() + "] " + this.url + " ping=" + this.ping() + " ServerState=" + this.status()
				+ " processingTime=" + this.serverProcesingTime() + " serverVersion=" + this.serverVersion();
		}
		return "[" + this.code() + "] " + this.url + " error=" + this.error;

	}

	private String serverVersion () {
		return "<" + this.serverVersion + ">";
	}

	private String serverProcesingTime () {
		if (this.serverProcesingTime < 60000L) {
			return this.serverProcesingTime + "";
		}
		return "<NOT REACHABLE>";
	}

	private String status () {
		return "<" + this.status + ">";
	}

	private String code () {
		if (this.code == -1) {
			return "X";
		}
		return this.code + "";
	}

	private String ping () {
		if (this.ping < 60000L) {
			return this.ping + "";
		}
		return "<NOT REACHABLE>";
	}

}
