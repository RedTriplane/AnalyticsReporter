
package com.jfixby.redreporter.desktop;

import com.jfixby.cmns.api.collections.Collection;
import com.jfixby.cmns.api.taskman.Job;
import com.jfixby.cmns.api.taskman.Task;
import com.jfixby.cmns.api.taskman.TaskManager;
import com.jfixby.cmns.api.taskman.TaskSpecs;

public class DesktopReporterService {

	private final DesktopReporter master;

	public DesktopReporterService (final DesktopReporter desktopReporter) {
		this.master = desktopReporter;
	}

	boolean isRunning = false;
	private Task task;

	public void start () {
		if (this.isRunning) {
			return;
		}
		final TaskSpecs specs = TaskManager.newTaskSpecs();
		specs.setName("red-reporter-service");
		specs.setRunInSeparatedThread(true);
		final Collection<Job> jobs = this.master.getJob();
		specs.addJobs(jobs);
		this.task = TaskManager.newTask(specs);
	}

}
