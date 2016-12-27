
package com.jfixby.redreporter.api.analytics;

import com.jfixby.redreporter.api.report.REPORT_URGENCY;
import com.jfixby.scarabei.api.collections.Collection;
import com.jfixby.scarabei.api.collections.Mapping;

public interface ReportWriter {

	public void dispose ();

	public void setAuthor (String authorID);

	public void setSubject (String subject);

	public void submitReport ();

	public void submitReport (REPORT_URGENCY urgency);

	public void addStringValues (Mapping<String, ?> stringValues);

	public void addStringValue (String key, Object value);

	void addException (String key, Throwable value);

	public void addStringValues (String string, Collection<String> msgs);

}
