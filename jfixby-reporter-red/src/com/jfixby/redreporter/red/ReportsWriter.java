
package com.jfixby.redreporter.red;

import com.jfixby.redreporter.api.Report;

public class ReportsWriter {

	private final RedReporter redReporter;

	public ReportsWriter (final RedReporter redReporter) {
		this.redReporter = redReporter;
	}

	public Report newReport () {
		final RedReport report = new RedReport(this.redReporter);

		return report;
	}

}
