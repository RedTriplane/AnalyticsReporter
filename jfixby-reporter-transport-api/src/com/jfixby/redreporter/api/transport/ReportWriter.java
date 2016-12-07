
package com.jfixby.redreporter.api.transport;

import com.jfixby.cmns.api.collections.Mapping;

public interface ReportWriter {

	public void dispose ();

	public void setAuthor (String authorID);

	public void setSubject (String subject);

	public void submitReport ();

	public void addStringValues (Mapping<String, ?> stringValues);

	public void addStringValue (String key, Object value);

}