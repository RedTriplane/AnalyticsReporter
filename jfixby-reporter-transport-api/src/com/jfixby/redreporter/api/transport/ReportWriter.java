
package com.jfixby.redreporter.api.transport;

public interface ReportWriter {

	public void dispose ();

	public void setAuthor (String authorID);

	public void setSubject (String subject);

	public void submitReport ();

}
