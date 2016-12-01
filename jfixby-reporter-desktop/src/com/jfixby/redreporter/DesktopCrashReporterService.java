
package com.jfixby.redreporter;

import com.jfixby.cmns.api.collections.Collection;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.taskman.Job;
import com.jfixby.cmns.api.taskman.Task;
import com.jfixby.cmns.api.taskman.TaskManager;
import com.jfixby.cmns.api.taskman.TaskSpecs;

public class DesktopCrashReporterService {

	final private Collection<Job> job;

	public DesktopCrashReporterService (final Collection<Job> collection) {
		this.job = collection;
	}

	boolean wasStarted = false;
	private Task task;

	public void start () {
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

}
