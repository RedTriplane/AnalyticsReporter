
package com.jfixby.redreporter;

import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.taskman.Job;
import com.jfixby.cmns.api.taskman.Task;
import com.jfixby.cmns.api.taskman.TaskManager;
import com.jfixby.cmns.api.taskman.TaskSpecs;

public class DesktopCrashReporterService {

	final private Job job;

	public DesktopCrashReporterService (final Job desktopReporter) {
		this.job = desktopReporter;
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
		specs.addJob(this.job);

		this.task = TaskManager.newTask(specs);
	}

}
