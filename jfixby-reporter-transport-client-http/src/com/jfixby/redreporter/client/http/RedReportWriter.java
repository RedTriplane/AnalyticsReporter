
package com.jfixby.redreporter.client.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.jfixby.redreporter.api.analytics.ReportWriter;
import com.jfixby.redreporter.api.report.REPORT_URGENCY;
import com.jfixby.redreporter.api.transport.REPORTER_PROTOCOL;
import com.jfixby.redreporter.api.transport.ReportData;
import com.jfixby.scarabei.api.collections.Collection;
import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.Map;
import com.jfixby.scarabei.api.collections.Mapping;
import com.jfixby.scarabei.api.collections.Pool;
import com.jfixby.scarabei.api.err.Err;
import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.io.IO;
import com.jfixby.scarabei.api.java.ByteArray;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.sys.Sys;
import com.jfixby.scarabei.api.sys.SystemInfo;
import com.jfixby.scarabei.api.sys.settings.SystemSettings;

public class RedReportWriter implements ReportWriter {
	private final ReporterHttpClient reporterHttpClient;
	private final Pool<RedReportWriter> pool;
	static long newID = 0;
	final Map<String, String> parameters = Collections.newMap();
	private String id;
	private File file;
	ReportData data;

	public RedReportWriter (final Pool<RedReportWriter> writersPool, final ReporterHttpClient reporterHttpClient) {
		this.pool = writersPool;
		this.reporterHttpClient = reporterHttpClient;
	}

	private synchronized String newID () {
		return System.currentTimeMillis() + "." + newID++;
	}

	public ByteArray serializeReport () {
		ByteArray bytes = null;
		try {
			bytes = IO.serialize(this.data);
		} catch (final IOException e) {
// e.printStackTrace();
			Err.reportError(e);
		}
		return bytes;
	}

	public ReportWriter start () {
		this.id = this.newID();
		final ReportsQueue Q = this.reporterHttpClient.queue;
		this.file = Q.getCache().child(this.id + CachedFilesFilter.FILE_NAME_SUFFIX);
		this.parameters.put(REPORTER_PROTOCOL.REPORT_VERSION, ReportData.REPORT_VERSION);
		this.parameters.put(REPORTER_PROTOCOL.REPORT_WRITTEN, Sys.SystemTime().currentTimeMillis() + "");
		this.data = new ReportData();
		return this;
	}

	@Override
	public void dispose () {
		this.id = null;
		this.file = null;
		this.parameters.clear();
		this.pool.free(this);
		this.data = null;
	}

	@Override
	public void setAuthor (final String authorID) {
		this.parameters.put(REPORTER_PROTOCOL.AUTHOR_ID, authorID);
	}

	@Override
	public void setSubject (final String subject) {
		this.parameters.put(REPORTER_PROTOCOL.SUBJECT, subject);
	}

	public File getFile () {
		return this.file;
	}

	public Mapping<? extends String, ? extends String> getParameters () {
		return this.parameters;
	}

	@Override
	public void addStringValues (final Mapping<String, ?> stringValues) {
		for (final String key : stringValues.keys()) {
			this.addStringValue(key, stringValues.get(key));
		}
	}

	@Override
	public void addStringValue (final String key, final Object value) {
		final LinkedHashMap<String, ArrayList<HashMap<String, String>>> collection = this.data.strings;
		String strValue = "";
		if (value != null) {
			strValue = value.toString();
		}
		addToCollection(key, strValue, collection);
	}

	@Override
	public void addException (final String key, final Throwable value) {
		final LinkedHashMap<String, ArrayList<HashMap<String, String>>> collection = this.data.exceptions;
		String strValue = "";
		if (value != null) {
			strValue = L.stackTraceToString(value);
		}
		addToCollection(key, strValue, collection);
	}

	static public void addToCollection (final String key, final String strValue,
		final LinkedHashMap<String, ArrayList<HashMap<String, String>>> collection) {
		ArrayList<HashMap<String, String>> list = collection.get(key);
		if (list == null) {
			list = new ArrayList<HashMap<String, String>>(2);
			collection.put(key, list);
		}
		final HashMap<String, String> stat = new HashMap<String, String>();
		stat.put(ReportData.PARAMETER_VALUE, strValue);
		stat.put(ReportData.PARAMETER_TIMESTAMP, System.currentTimeMillis() + "");
		list.add(stat);

	}

	@Override
	public void addStringValues (final String key, final Collection<String> msgs) {
		for (final String msg : msgs) {
			this.addStringValue(key, msg);
		}
	}

	@Override
	public void submitReport (REPORT_URGENCY urgency) {
		if (urgency == null) {
			urgency = REPORT_URGENCY.NORMALL;
		}
		final RedReport report = new RedReport(this);
		report.setUrgency(urgency);
		this.reporterHttpClient.queue.submit(report);
	}

	@Override
	public void submitReport () {
		this.submitReport(REPORT_URGENCY.NORMALL);
	}

	@Override
	public void includeSystemSettings () {
		final Mapping<String, String> print = SystemSettings.listAllSettings();
		this.addStringValues(print);
	}

	@Override
	public void includeSystemInfo () {
		final SystemInfo info = Sys.getSystemInfo();
		this.addStringValues(info.listParameters());
	}

}
