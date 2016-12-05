
package com.jfixby.redreporter.client.http;

import com.jfixby.cmns.api.collections.Mapping;
import com.jfixby.cmns.api.collections.Pool;
import com.jfixby.cmns.api.err.Err;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.java.ByteArray;
import com.jfixby.redreporter.api.report.Report;
import com.jfixby.redreporter.api.transport.ReportWriter;

public class RedReportWriter implements ReportWriter {

	private final Pool<RedReportWriter> pool;

	public RedReportWriter (final Pool<RedReportWriter> writersPool) {
		this.pool = writersPool;
	}

	@Override
	public void dispose () {
		this.pool.free(this);
	}

	@Override
	public void setAuthor (final String authorID) {
		Err.throwNotImplementedYet();
	}

	@Override
	public void setSubject (final String subject) {
		Err.throwNotImplementedYet();
	}

	@Override
	public Report produceReport () {
		Err.throwNotImplementedYet();
		return null;
	}

	@Override
	public void submitReport () {
		Err.throwNotImplementedYet();
	}

	public ByteArray serializeReport () {
		return null;
	}

	public File getFile () {
		return null;
	}

	public Mapping<? extends String, ? extends String> getParameters () {
		return null;
	}

}
