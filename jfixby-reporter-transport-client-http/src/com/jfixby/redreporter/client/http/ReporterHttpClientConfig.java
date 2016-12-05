
package com.jfixby.redreporter.client.http;

import com.jfixby.cmns.api.collections.Collection;
import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.Set;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.net.http.HttpURL;
import com.jfixby.cmns.api.taskman.TASK_TYPE;

public class ReporterHttpClientConfig {
	final Set<HttpURL> servers = Collections.newSet();
	private File iidStorage;
	private String IIDFileName;
	private File logs;
	private TASK_TYPE taskType = TASK_TYPE.SEPARATED_THREAD;

	public void addAnalyticsServerUrl (final HttpURL url) {
		this.servers.add(url);
// this.servers.print("+" + url);
	}

	public Collection<HttpURL> listServers () {
		return this.servers;
	}

	public void setInstallationIDStorageFolder (final File iidStorage) {
		this.iidStorage = iidStorage;
	}

	public File getInstallationIDStorageFolder () {
		return this.iidStorage;
	}

	public String getIIDFileName () {
		return this.IIDFileName;
	}

	public void setIIDFileName (final String installationIdFileName) {
		this.IIDFileName = installationIdFileName;
	}

	public void setCacheFolder (final File logs) {
		this.logs = logs;
	}

	public File getCacheFolder () {
		return this.logs;
	}

	public TASK_TYPE getTaskType () {
		return this.taskType;
	}

	public void setTaskType (final TASK_TYPE taskType) {
		this.taskType = taskType;
	}

}
