
package com.jfixby.redreporter.crash;

import com.jfixby.redreporter.api.analytics.AnalyticsReporter;
import com.jfixby.redreporter.api.analytics.ReportWriter;
import com.jfixby.scarabei.api.err.Err;
import com.jfixby.scarabei.api.err.ErrorComponent;
import com.jfixby.scarabei.api.err.NotImplementedYetException;
import com.jfixby.scarabei.api.lambda.VoidAction;
import com.jfixby.scarabei.red.err.RedError;

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
	public void reportError (final String message) {
		final ReportWriter writer = AnalyticsReporter.newReportWriter();
		writer.setAuthor(this.authorID);
		writer.setSubject("SEVERE CRASH");
		writer.addStringValue("message", message);
		final ReportObserver reportObserver = new ReportObserver(new VoidAction() {
			@Override
			public void act () {
				RedReporterErrorsListener.this.defaultErrorListener.reportError(message);
			}
		});
		writer.submitReport(reportObserver);

	}

	@Override
	public void reportError (final Throwable e) {
		final ReportWriter writer = AnalyticsReporter.newReportWriter();
		writer.setAuthor(this.authorID);
		writer.setSubject("SEVERE CRASH");
		writer.addException("error", e);
		final ReportObserver reportObserver = new ReportObserver(new VoidAction() {
			@Override
			public void act () {
				RedReporterErrorsListener.this.defaultErrorListener.reportError(e);
			}
		});
		writer.submitReport(reportObserver);
	}

	@Override
	public void reportError (final String message, final Throwable e) {
		final ReportWriter writer = AnalyticsReporter.newReportWriter();
		writer.setAuthor(this.authorID);
		writer.setSubject("SEVERE CRASH");
		writer.addStringValue("message", message);
		writer.addException("error", e);
		final ReportObserver reportObserver = new ReportObserver(new VoidAction() {
			@Override
			public void act () {
				RedReporterErrorsListener.this.defaultErrorListener.reportError(message, e);
			}
		});
		writer.submitReport(reportObserver);
	}

	@Override
	public void reportNotImplementedYet () {
		final ReportWriter writer = AnalyticsReporter.newReportWriter();
		writer.setAuthor(this.authorID);
		writer.setSubject("NotImplementedYet");
		writer.addException("error", new NotImplementedYetException());
		final ReportObserver reportObserver = new ReportObserver(new VoidAction() {
			@Override
			public void act () {
				RedReporterErrorsListener.this.defaultErrorListener.reportNotImplementedYet();
			}
		});

		writer.submitReport(reportObserver);
	}

	@Override
	public void reportGCLeak (final String msg, final Object leakingObject) {
		final ReportWriter writer = AnalyticsReporter.newReportWriter();
		writer.setAuthor(this.authorID);
		writer.setSubject("GC LEAK");
		writer.addStringValue("message", msg);
		writer.addStringValue("leakingObject", leakingObject);
		final ReportObserver reportObserver = new ReportObserver(new VoidAction() {
			@Override
			public void act () {
				RedReporterErrorsListener.this.defaultErrorListener.reportGCLeak(msg, leakingObject);
			}
		});
		writer.submitReport(reportObserver);
	}

	@Override
	public void reportError (final Thread t, final Throwable e) {
		final ReportWriter writer = AnalyticsReporter.newReportWriter();
		writer.setAuthor(this.authorID);
		writer.setSubject("SEVERE CRASH");
		writer.addException("error", e);
		writer.addStringValue("thread", t);
		final ReportObserver reportObserver = new ReportObserver(new VoidAction() {
			@Override
			public void act () {
				RedReporterErrorsListener.this.defaultErrorListener.reportError(t, e);
			}
		});
		writer.submitReport(reportObserver);
	}

	private final String authorID = ("com.red-triplane.reporter.err");

	boolean enabled = false;

	public void enable () {
		if (this.enabled) {
			return;
		}
		this.enabled = true;
		final ErrorComponent oldErr = Err.component();
		if (oldErr != null) {
			Err.deInstallCurrentComponent();
			this.setChildListener(oldErr);
		}
		this.deploy();
		Err.installComponent(this);
	}

	public void disable () {
		if (!this.enabled) {
			return;
		}
		this.enabled = !true;
		Err.deInstallCurrentComponent();
		Err.installComponent(this.getChildListener());
	}

	public boolean isEnabled () {
		return this.enabled;
	}

}
