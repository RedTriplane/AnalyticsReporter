
package com.jfixby.redreporter.glassfish;

import javax.servlet.ServletOutputStream;

import com.jfixby.cmns.api.assets.ID;
import com.jfixby.cmns.api.collections.List;
import com.jfixby.cmns.api.collections.Map;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.net.message.Message;

public class RedReporterEntryPointArguments {

	public long request_number;
	public ID requestID;
	public Message message;
	public ServletOutputStream server_to_client_stream;
	public long timestamp;
	public Map<String, List<String>> inputHeaders;

	public void print () {
		L.d("---[" + this.request_number + "]-----------------------------------");
		L.d("       requestID", this.requestID);
		L.d("       timestamp", this.timestamp);
	}

}
