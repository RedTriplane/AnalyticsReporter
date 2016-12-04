
package com.jfixby.redreporter.api.analytics;

import com.jfixby.cmns.api.collections.Mapping;
import com.jfixby.cmns.api.java.ByteArray;

public interface Report {

	public ByteArray getPackedData ();

	public Mapping<String, String> listParameters ();

	public void dispose ();

}
