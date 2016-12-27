
package com.jfixby.redreporter.client.http;

import java.io.IOException;

import com.jfixby.redreporter.api.report.REPORT_URGENCY;
import com.jfixby.redreporter.api.report.Report;
import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.Map;
import com.jfixby.scarabei.api.collections.Mapping;
import com.jfixby.scarabei.api.debug.Debug;
import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.java.ByteArray;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.util.JUtils;

public class RedReport implements Report {

	@Override
	public String toString () {
		return "RedReport[" + this.parameters + "]";
	}

	private final File file;
	private ByteArray packedData;
	private final Map<String, String> parameters = Collections.newMap();
	private final RedReportWriter writer;
	boolean isCached = false;
	private REPORT_URGENCY urgency;

	private RedReport (final File file, final Map<String, String> parameters, final ByteArray data) {
		this.writer = null;
		this.file = Debug.checkNull("report file", file);
		this.parameters.putAll(Debug.checkNull("parameters", parameters));
		this.packedData = data;
		this.isCached = true;
	}

	RedReport (final RedReportWriter writer) {
		this.writer = Debug.checkNull("writer", writer);
		this.file = Debug.checkNull("report file", writer.getFile());
		this.parameters.putAll(Debug.checkNull("parameters", writer.getParameters()));
		this.packedData = null;
		this.isCached = false;
	}

	@Override
	public Mapping<String, String> listParameters () {
		return this.parameters;
	}

	@Override
	public ByteArray getPackedData () {
		if (this.packedData != null) {
			return this.packedData;
		}
		this.packedData = this.writer.serializeReport();
		return this.packedData;
	}

	@Override
	public void dispose () {
		try {
			if (!this.file.exists()) {
				return;
			}
			L.d("deleting", this.file);
			this.file.delete();
		} catch (final IOException e) {
			L.e("failed to delete report file " + this.file, e);
		}
		if (this.writer == null) {
			return;
		}
		this.writer.dispose();
	}

	public static RedReport readFromFile (final File file) {
		try {
			L.d("reading", file);

			final SrlzdReport reportData = file.readData(SrlzdReport.class);
			Map<String, String> params = null;
			if (reportData.sendParameters != null) {
				params = Collections.newMap(reportData.sendParameters);
			} else {
				L.e("SrlzdReport.sendParameters == null", file);
				return null;
			}
			ByteArray bytes = null;
			if (reportData.serializedReport != null) {
				bytes = JUtils.newByteArray(reportData.serializedReport);
			} else {
				L.e("SrlzdReport.serializedReport == null", file);
				return null;
			}

			final RedReport report = new RedReport(file, params, bytes);
			return report;
		} catch (final IOException e) {
			L.e("failed to read report file: " + file, e);
		}
		return null;
	}

	public static final boolean writeToFile (final RedReport report) {
		try {
			L.d("writing", report.file);
			final SrlzdReport reportData = new SrlzdReport();
			reportData.sendParameters.putAll(report.listParameters().toJavaMap());
			reportData.serializedReport = report.getPackedData().toArray();
			report.file.writeData(reportData);
			report.isCached = true;
			return true;
		} catch (final IOException e) {
			L.e("failed to save report file " + report.file, e);
			return false;
		}
	}

	@Override
	public boolean ensureCached () {
		if (this.isCached) {
			return true;
		}
		final boolean success = writeToFile(this);

		return success;
	}

	public void setUrgency (final REPORT_URGENCY urgency) {
		this.urgency = urgency;
		if (urgency == null) {
			this.urgency = REPORT_URGENCY.NORMALL;
		}
	}

	@Override
	public REPORT_URGENCY getUrgency () {
		return this.urgency;
	}

}
