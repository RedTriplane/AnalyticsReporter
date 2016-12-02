
package com.jfixby.redreporter;

import java.io.IOException;

import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.err.Err;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.io.IO;
import com.jfixby.cmns.api.java.ByteArray;
import com.jfixby.cmns.api.json.Json;
import com.jfixby.cmns.api.json.JsonString;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.util.JUtils;
import com.jfixby.redreporter.api.analytics.Report;

public class RedReport implements Report {

	static long freeID = 1000000000;

	private File file = null;

	private String local_id = null;
	private Long timestamp = null;

	private JsonString packed = null;

	boolean failedToCache = false;

	public RedReport () {
		this.local_id = newID();
		this.timestamp = System.currentTimeMillis();
	}

	private RedReport (final File file, final JsonString data) {
		this.file = file;
		this.packed = data;
		this.failedToCache = false;
	}

	synchronized static private String newID () {
		return "" + freeID++;
	}

	public boolean cache (final File cacheFolder, final String extention) {
		Debug.checkNull("cacheFolder", cacheFolder);
		if (this.file != null) {
			return true;
		}
		if (this.failedToCache) {
			return false;
		}
		Debug.checkNull("local_id", this.local_id);
		this.file = cacheFolder.child(this.local_id + extention);
		final JsonString stringData = this.toPackedString();
		final ByteArray compressed = IO.compress(JUtils.newByteArray(stringData.toString().getBytes()));
		try {
			L.d("writing", this.file);
			this.file.writeBytes(compressed);
			return true;
		} catch (final IOException e) {
			this.file = null;
			this.failedToCache = true;
			e.printStackTrace();
			Err.reportWarning("failed to save report file " + this.file, e);
			return false;
		}

	}

	@Override
	public JsonString toPackedString () {
		if (this.packed != null) {
			return this.packed;
		}

		final SrlzdReport data = new SrlzdReport();
		data.local_id = this.local_id;
		data.timestamp = this.timestamp;
		this.packed = Json.serializeToString(data);
		return this.packed;
	}

	public void dispose () {
		if (this.file == null) {
			return;
		}
		try {
			this.file.delete();
		} catch (final IOException e) {
			e.printStackTrace();
			Err.reportWarning("failed to delete report file " + this.file, e);
		}
		this.file = null;

	}

	public static RedReport readFromCache (final File file) {
		try {
			L.d("reading", file);
			final ByteArray bytes = file.readBytes();
			final ByteArray unzip = IO.decompress(bytes);
			final String raw_json_string = JUtils.newString(unzip);
			final JsonString data = Json.newJsonString(raw_json_string);
			final RedReport report = new RedReport(file, data);
			return report;
		} catch (final IOException e) {
			e.printStackTrace();
			Err.reportWarning("failed to read report file: " + file, e);
		}
		return null;
	}

}
