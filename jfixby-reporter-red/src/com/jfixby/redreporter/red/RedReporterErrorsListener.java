
package com.jfixby.redreporter.red;

import com.jfixby.cmns.api.err.ErrorComponent;
import com.jfixby.cmns.api.err.NotImplementedYetException;
import com.jfixby.red.err.RedError;
import com.jfixby.redreporter.api.err.ErrorReporterComponent;

public class RedReporterErrorsListener implements ErrorComponent {

	private final ErrorReporterComponent master;
	private ErrorComponent defaultErrorListener;

	public RedReporterErrorsListener (final RedReporter redReporter) {
		this.master = redReporter;
	}

	public void setChildListener (final ErrorComponent defaultErrorListener) {
		this.defaultErrorListener = defaultErrorListener;
	}

	public void deploy () {
		if (this.defaultErrorListener == null) {
			this.defaultErrorListener = new RedError();
		}
	}

	@Override
	public void reportWarning (final String message) {
// final Report report = this.master.newReport();
// report.addWarning(message);
// report.submit();
// this.defaultErrorListener.reportWarning(message);
	}

	@Override
	public void reportError (final String message) {
// final Report report = this.master.newReport();
// report.addError(message);
// report.submit();
// this.defaultErrorListener.reportError(message);

	}

	@Override
	public void reportError (final Throwable e) {
// final Report report = this.master.newReport();
// report.addError(e);
// report.submit();
// this.defaultErrorListener.reportError(e);

	}

	@Override
	public void reportError (final String message, final Throwable e) {
// final Report report = this.master.newReport();
// report.addError(message);
// report.addError(e);
// report.submit();
// this.defaultErrorListener.reportError(message, e);

	}

	@Override
	public void reportNotImplementedYet () {
		this.reportError(new NotImplementedYetException());
	}

	@Override
	public void reportGCLeak (final String msg) {
// final Report report = this.master.newReport();
// report.reportGCLeak(msg);
//// report.addError(e);
// report.submit();
// this.defaultErrorListener.reportGCLeak(msg);
	}

}
