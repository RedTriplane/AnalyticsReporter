
package com.jfixby.redreporter;

import com.jfixby.cmns.api.collections.Collection;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.taskman.Job;
import com.jfixby.cmns.api.taskman.Task;
import com.jfixby.cmns.api.taskman.TaskManager;
import com.jfixby.cmns.api.taskman.TaskSpecs;

public class DesktopCrashReporter extends RedCrashReporter {

	public DesktopCrashReporter (final DesktopReporterConfig deskCfg) {
		super(deskCfg.getTransport(), deskCfg.getLogsCache());
	}

	final private Collection<Job> job = null;

	boolean wasStarted = false;
	private Task task;

	void start () {
		if (this.task != null && this.task.isActive()) {
			L.e("DesktopCrashReporterService was already started");
			return;
		}
		final TaskSpecs specs = TaskManager.newTaskSpecs();
		specs.setName("red-reporter-service");
		specs.setRunInSeparatedThread(true);
		specs.addJobs(this.job);

		this.task = TaskManager.newTask(specs);
	}

	@Override
	public void deploy () {
		this.loadReportsFromCache();
	}

}
