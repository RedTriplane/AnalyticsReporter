
package com.jfixby.redreporter.glassfish;

import com.jfixby.scarabei.api.java.ByteArray;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.net.http.Http;
import com.jfixby.scarabei.api.net.http.HttpConnection;
import com.jfixby.scarabei.api.net.http.HttpConnectionInputStream;
import com.jfixby.scarabei.api.net.http.HttpURL;
import com.jfixby.scarabei.api.util.JUtils;

public class AWSServletUtil {
	public static final String readInstanceID () {
		String instance_id;
		try {
			final String url_string = "http://169.254.169.254/latest/meta-data/instance-id";
			final HttpURL url = Http.newURL(url_string);
			final HttpConnection connect = Http.newConnection(url);
			connect.open();
			final HttpConnectionInputStream is = connect.getInputStream();
			is.open();
			final ByteArray data = is.readAll();
			is.close();
			connect.close();
			instance_id = JUtils.newString(data);
		} catch (final Exception e) {
			L.e("failed to get instance id", e + "");
			instance_id = "no_instance_id-" + System.currentTimeMillis();
		}
		return instance_id;
	}
}
