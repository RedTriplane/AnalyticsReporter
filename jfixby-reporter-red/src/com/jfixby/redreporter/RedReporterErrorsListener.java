
package com.jfixby.redreporter;

import com.jfixby.cmns.api.err.ErrorComponent;
import com.jfixby.cmns.api.err.NotImplementedYetException;
import com.jfixby.red.err.RedError;
import com.jfixby.redreporter.api.analytics.Report;

public class RedReporterErrorsListener implements ErrorComponent {

	private final RedCrashReporter master;
	private ErrorComponent defaultErrorListener;
	private ErrorComponent child;

	public RedReporterErrorsListener (final RedCrashReporter redReporter) {
		this.master = redReporter;
	}

	public void setChildListener (final ErrorComponent defaultErrorListener) {
		this.defaultErrorListener = defaultErrorListener;
		this.child = defaultErrorListener;
	}

	public ErrorComponent getChildListener () {
		return this.child;
	}

	public void deploy () {
		if (this.defaultErrorListener == null) {
			this.defaultErrorListener = new RedError();
		}
	}

	@Override
	public void reportWarning (final String message) {
		final Report report = this.master.newReport();
// report.addWarning(message);
// report.submit();
		this.defaultErrorListener.reportWarning(message);
	}

	@Override
	public void reportError (final String message) {
// final Report report = this.master.newReport();
// report.addError(message);
// report.submit();
		this.defaultErrorListener.reportError(message);

	}

	@Override
	public void reportError (final Throwable e) {
// final Report report = this.master.newReport();
// report.addError(e);
// report.submit();
		this.defaultErrorListener.reportError(e);

	}

	@Override
	public void reportError (final String message, final Throwable e) {
// final Report report = this.master.newReport();
// report.addError(message);
// report.addError(e);
// report.submit();
		this.defaultErrorListener.reportError(message, e);

	}

	@Override
	public void reportNotImplementedYet () {
		this.reportError(new NotImplementedYetException());
	}

	@Override
	public void reportGCLeak (final String msg, final Object leakingObject) {
// final Report report = this.master.newReport();
// report.reportGCLeak(msg);
//// report.addError(e);
// report.submit();
		this.defaultErrorListener.reportGCLeak(msg, leakingObject);
	}

	@Override
	public void reportError (final Thread t, final Throwable e) {
		this.defaultErrorListener.reportError(t, e);

	}

	@Override
	public void reportWarning (final String msg, final Throwable e) {
		this.defaultErrorListener.reportWarning(msg, e);

	}

}
