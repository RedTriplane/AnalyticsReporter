
package com.jfixby.redreporter.client.http;

import java.io.IOException;

import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.Map;
import com.jfixby.cmns.api.collections.Mapping;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.java.ByteArray;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.util.JUtils;
import com.jfixby.redreporter.api.analytics.Report;

public class RedReport implements Report {

	private RedReport (final File file, final Map<String, String> parameters, final ByteArray data) {
		this.file = file;
		this.packed = data;
		if (parameters != null) {
			this.parameters.putAll(parameters);
		}
		this.failedToCache = false;
	}

	private final File file;
	private final ByteArray packed;
	private final Map<String, String> parameters = Collections.newMap();
	boolean failedToCache = false;

	@Override
	public Mapping<String, String> listParameters () {
		return this.parameters;
	}

	@Override
	public ByteArray getPackedData () {
		return this.packed;
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
			}
			ByteArray bytes = null;
			if (reportData.serializedReport != null) {
				bytes = JUtils.newByteArray(reportData.serializedReport);
			} else {
				L.e("SrlzdReport.serializedReport == null", file);
			}

			final RedReport report = new RedReport(file, params, bytes);
			return report;
		} catch (final IOException e) {
			L.e("failed to read report file: " + file, e);
		}
		return null;
	}

	public static final boolean writeToFile (final RedReport report, final File file) {
		try {
			L.d("writing", file);
			final SrlzdReport reportData = new SrlzdReport();
			reportData.sendParameters.putAll(report.listParameters().toJavaMap());
			reportData.serializedReport = report.getPackedData().toArray();
			file.writeData(reportData);
			return true;
		} catch (final IOException e) {
			L.e("failed to save report file " + file, e);
			return false;
		}
	}

}
