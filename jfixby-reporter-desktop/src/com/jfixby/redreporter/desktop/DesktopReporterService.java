
package com.jfixby.redreporter.desktop;

import com.jfixby.cmns.api.taskman.TaskManager;

public class DesktopReporterService {

	private final DesktopReporter master;

	public DesktopReporterService (final DesktopReporter desktopReporter) {
		this.master = desktopReporter;
	}

	boolean isRunning = false;

	public void start () {
		if (this.isRunning) {
			return;
		}
		TaskManager.newTask("red-reporter-service", this.master.getJob());
	}

}
