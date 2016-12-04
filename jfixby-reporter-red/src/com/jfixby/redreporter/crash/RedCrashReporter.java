
package com.jfixby.redreporter.crash;

import com.jfixby.cmns.api.assets.ID;
import com.jfixby.cmns.api.assets.Names;
import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.redreporter.api.crash.CrashReporterComponent;
import com.jfixby.redreporter.api.transport.ReporterTransport;

public class RedCrashReporter implements CrashReporterComponent {

	private final ID senderID = Names.newID("com.red-triplane.reporter.crash");
	private final ReporterTransport transport;

	public RedCrashReporter (final ReporterTransport transport) {
		this.transport = Debug.checkNull("transport", transport);
	}

	final RedReporterUncaughtExceptionHandler uncaughtExceptionHandler = new RedReporterUncaughtExceptionHandler();
	final RedReporterErrorsListener errorsListener = new RedReporterErrorsListener(this);
	final RedReporterLoggerListener logsListener = new RedReporterLoggerListener(this);

	public ReporterTransport getTransport () {
		return this.transport;
	}

	@Override
	public String toString () {
		return "RedCrashReporter[" + this.senderID + "]";
	}

	@Override
	public void enableUncaughtExceptionHandler () {
		this.uncaughtExceptionHandler.enable();
	}

	@Override
	public void enableErrorsListener () {
		this.errorsListener.enable();
	}

	@Override
	public void enableLogsListener () {
		this.logsListener.enable();
	}

	@Override
	public void disableUncaughtExceptionHandler () {
		this.uncaughtExceptionHandler.disable();
	}

	@Override
	public void disableErrorsListener () {
		this.errorsListener.disable();
	}

	@Override
	public void disableLogsListener () {
		this.logsListener.disable();
	}

	@Override
	public boolean isUncaughtExceptionHandlerEnabled () {
		return this.uncaughtExceptionHandler.isEnabled();
	}

	@Override
	public boolean isErrorsListenerEnabled () {
		return this.errorsListener.isEnabled();
	}

	@Override
	public boolean isLogsListenerEnabled () {
		return this.logsListener.isEnabled();
	}

}
