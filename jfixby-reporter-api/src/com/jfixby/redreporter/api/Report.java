
package com.jfixby.redreporter.api;

public interface Report {

	public static final String WARNING = "Report.WARNING";
	public static final String ERROR = "Report.ERROR";
	public static final String GCLEAK = "Report.GCLEAK";
	public static final String INFO = "Report.INFO";

	void addWarning (String message);

	void addError (String message);

	void addError (Throwable e);

	void reportGCLeak (String msg);

	void submit ();

	void setPriority (PRIORITY priority);

}
