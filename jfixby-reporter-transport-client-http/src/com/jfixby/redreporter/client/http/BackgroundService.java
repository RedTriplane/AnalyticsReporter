
package com.jfixby.redreporter.client.http;

import com.jfixby.cmns.api.collections.Collection;
import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.List;
import com.jfixby.cmns.api.taskman.Job;

public class BackgroundService {
	final LoadCacheJob loadCache = new LoadCacheJob(this);
	final PushServiceJob pushService = new PushServiceJob(this);
	final List<Job> serviceJob = Collections.newList(this.loadCache, this.pushService);

	public Collection<Job> getServiceJob () {
		return this.serviceJob;
	}
}
