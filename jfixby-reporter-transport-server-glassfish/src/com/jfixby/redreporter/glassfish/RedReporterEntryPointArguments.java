
package com.jfixby.redreporter.glassfish;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;

import com.jfixby.scarabei.api.assets.ID;
import com.jfixby.scarabei.api.collections.List;
import com.jfixby.scarabei.api.collections.Map;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.net.message.Message;

public class RedReporterEntryPointArguments {

	public long request_number;
	public ID requestID;
	public Message message;
	public ServletOutputStream server_to_client_stream;
	public long timestamp;
	public Map<String, List<String>> inputHeaders;
	public ServletInputStream client_to_server_stream;
	public boolean isHeathCheck = false;
	public long receivedTimestamp;
	public String sentTimestamp;
	public String versionString;
	public String writtenTimestamp;
	public String token;
	public Long installID;
	public byte[] resializedBody;
	public String sessionID;
	public String subject;
	public String author;

	public void print () {
		L.d("---[" + this.request_number + "]-----------------------------------");
		L.d("       requestID", this.requestID);
		L.d("       timestamp", this.timestamp);
	}

}
