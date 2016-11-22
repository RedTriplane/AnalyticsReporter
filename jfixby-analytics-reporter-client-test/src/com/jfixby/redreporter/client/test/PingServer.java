
package com.jfixby.redreporter.client.test;

import java.io.IOException;

import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.file.LocalFileSystem;
import com.jfixby.red.desktop.DesktopSetup;
import com.jfixby.redreporter.api.AnalyticsReporter;
import com.jfixby.redreporter.client.ClientConfig;
import com.jfixby.redreporter.client.desktop.DesktopReporter;

public class PingServer {

	public static void main (final String[] args) throws IOException {
		DesktopSetup.deploy();

		final File cache = LocalFileSystem.ApplicationHome().child("report-cache");
		final ClientConfig config = new ClientConfig();
		final config.set

		AnalyticsReporter.installComponent(new DesktopReporter(cache));

	}

}