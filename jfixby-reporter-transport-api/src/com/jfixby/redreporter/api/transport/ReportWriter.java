
package com.jfixby.redreporter.api.transport;

import com.jfixby.redreporter.api.report.Report;

public interface ReportWriter {

	public void dispose ();

	public void setAuthor (String authorID);

	public void setSubject (String subject);

	public Report produceReport ();

	public void submitReport ();

}
