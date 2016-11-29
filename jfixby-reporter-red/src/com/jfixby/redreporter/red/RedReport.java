
package com.jfixby.redreporter.red;

import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.List;
import com.jfixby.cmns.api.collections.Map;
import com.jfixby.cmns.api.err.Err;
import com.jfixby.redreporter.api.PRIORITY;
import com.jfixby.redreporter.api.analytics.Report;

public class RedReport implements Report {
	boolean submited = false;
	private final long timestamp;

	final Map<String, List<RedReportMessage>> group_message = Collections.newMap();

	static long ID = 0;
	long id = 0;
	private final RedReporter master;

	public RedReport (final RedReporter redReporter) {
		this.master = redReporter;
		this.timestamp = System.currentTimeMillis();
		this.id = ID++;
// this.group_message.put(Report.WARNING, this.newList());
// this.group_message.put(Report.ERROR, this.newList());
// this.group_message.put(Report.GCLEAK, this.newList());
// this.group_message.put(Report.INFO, this.newList());

	}

	private List<RedReportMessage> newList () {
		return Collections.newList();
	}

	@Override
	public void submit () {
		if (this.submited) {
			Err.reportWarning("report is already submitted " + this);
		}
		this.master.submit(this);
		this.submited = true;
	}

	@Override
	public void addWarning (final String message) {
		final List<RedReportMessage> bag = this.getBag(Report.WARNING);
	}

	private List<RedReportMessage> getBag (final String tag) {
		List<RedReportMessage> bag = this.group_message.get(tag);
		if (bag == null) {
			bag = Collections.newList();
			this.group_message.put(tag, bag);
		}
		return bag;
	}

	@Override
	public void addError (final String message) {
		final List<RedReportMessage> bag = this.getBag(Report.ERROR);
		final RedReportMessage messgae = new RedReportMessage(Report.ERROR, message);
		bag.add(messgae);
	}

	@Override
	public void addError (final Throwable e) {
		final List<RedReportMessage> bag = this.getBag(Report.ERROR);
		final RedReportMessage messgae = new RedReportMessage(Report.ERROR, e);
		bag.add(messgae);
	}

	@Override
	public void reportGCLeak (final String msg) {
		final List<RedReportMessage> bag = this.getBag(Report.GCLEAK);
		final RedReportMessage messgae = new RedReportMessage(Report.GCLEAK, msg);
		bag.add(messgae);
	}

	@Override
	public String toString () {
		return "RedReport [id=" + this.id + ", timestamp=" + this.timestamp + "]";
	}

	@Override
	public void setPriority (final PRIORITY priority) {
	}

}
