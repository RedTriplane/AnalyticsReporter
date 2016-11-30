
package com.jfixby.redreporter.client.http;

public interface ServerRanker {

	public void onSuccess (ServerHandler server, ServerPing result);

	public void onFail (ServerHandler server, ServerPing result);

}
