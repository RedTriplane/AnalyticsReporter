
package com.jfixby.redreporter.api.report;

import com.jfixby.scarabei.api.collections.Mapping;
import com.jfixby.scarabei.api.java.ByteArray;

public interface Report {

	public ByteArray getPackedData ();

	public Mapping<String, String> listParameters ();

	public void dispose ();

	public boolean ensureCached ();

}
