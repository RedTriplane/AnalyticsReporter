
package com.jfixby.redreporter.crash.desktop;

import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.taskman.Task;
import com.jfixby.cmns.api.taskman.TaskManager;
import com.jfixby.cmns.api.taskman.TaskSpecs;

public class DesktopReporterService {

	private final DesktopCrashReporter master;

	public DesktopReporterService (final DesktopCrashReporter desktopReporter) {
		this.master = desktopReporter;
	}

	boolean isRunning = false;
	private Task task;

	public void start () {
		if (this.isRunning) {
			L.e("desktop RR service is already started");
			return;
		}
		final TaskSpecs specs = TaskManager.newTaskSpecs();
		specs.setName("red-reporter-service");
		specs.setRunInSeparatedThread(true);

		this.task = TaskManager.newTask(specs);
	}

	public void stop () {
	}

}
