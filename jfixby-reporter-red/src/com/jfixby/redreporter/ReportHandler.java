
package com.jfixby.redreporter;

import java.io.IOException;

import com.jfixby.cmns.api.err.Err;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.io.IO;
import com.jfixby.cmns.api.java.ByteArray;
import com.jfixby.cmns.api.json.Json;
import com.jfixby.cmns.api.json.JsonString;
import com.jfixby.cmns.api.util.JUtils;
import com.jfixby.redreporter.api.analytics.Report;

public class ReportHandler {

	static long freeID = 10000000;

	private Report data;
	private File file;

	public ReportHandler () {

	}

	void pack () {
		this.data = new Report();
		this.data.local_id = newID();
		this.data.timestamp = System.currentTimeMillis();

	}

	synchronized static private String newID () {
		return System.currentTimeMillis() + "." + freeID++;
	}

	public boolean cache (final File cacheFolder, final String extention) {
		if (this.data == null) {
			this.pack();
		}
		this.file = cacheFolder.child(this.getData().local_id + extention);
		final JsonString json = Json.serializeToString(this.data);
		final ByteArray compressed = IO.compress(JUtils.newByteArray(json.toString().getBytes()));
		try {
			this.file.writeBytes(compressed);
			return true;
		} catch (final IOException e) {
			e.printStackTrace();
			Err.reportWarning("failed to save report file " + this.file, e);
			return false;
		}

	}

	Report getData () {
		if (this.data == null) {
			this.pack();
		}
		return this.data;
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
		this.data = null;

	}

	public boolean readFromFile (final File file) {
		this.file = file;
		try {
			final ByteArray bytes = file.readBytes();
			final ByteArray unzip = IO.decompress(bytes);
			this.data = Json.deserializeFromString(Report.class, JUtils.newString(unzip.toArray()));
			return true;
		} catch (final IOException e) {
			e.printStackTrace();
			Err.reportWarning("failed to read report file: " + file, e);
		}
		return false;
	}

}
