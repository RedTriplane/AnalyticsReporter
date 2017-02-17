/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jfixby.redreporter.glassfish;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfixby.jar.loader.RanaJarLoader;
import com.jfixby.rana.api.asset.AssetsManager;
import com.jfixby.rana.api.asset.AssetsManagerFlags;
import com.jfixby.rana.api.pkg.PackageReaderListener;
import com.jfixby.rana.api.pkg.ResourcesGroup;
import com.jfixby.rana.api.pkg.ResourcesManager;
import com.jfixby.rana.api.pkg.ResourcesManagerComponent;
import com.jfixby.redreporter.server.api.HealthReportType;
import com.jfixby.redreporter.server.api.ReporterServer;
import com.jfixby.redreporter.server.api.ServerCoreConfig;
import com.jfixby.scarabei.adopted.gdx.json.GoogleGson;
import com.jfixby.scarabei.amazon.aws.RedAWS;
import com.jfixby.scarabei.api.assets.ID;
import com.jfixby.scarabei.api.assets.Names;
import com.jfixby.scarabei.api.collections.Collection;
import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.List;
import com.jfixby.scarabei.api.desktop.ScarabeiDesktop;
import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.file.LocalFileSystem;
import com.jfixby.scarabei.api.json.Json;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.net.http.Http;
import com.jfixby.scarabei.api.net.http.HttpURL;
import com.jfixby.scarabei.api.sys.settings.SystemSettings;
import com.jfixby.scarabei.aws.api.AWS;
import com.jfixby.scarabei.db.api.DB;
import com.jfixby.scarabei.db.api.DBConfig;
import com.jfixby.scarabei.db.api.DataBase;
import com.jfixby.scarabei.db.mysql.MySQLDB;

public abstract class RedReporterEntryPoint extends HttpServlet {
	static final RequestProcessor processor;
	private static final long serialVersionUID = -1649148797847741708L;

	static {
		ScarabeiDesktop.deploy();
		Json.installComponent(new GoogleGson());
		DB.installComponent(new MySQLDB());
		AWS.installComponent(new RedAWS());

		processor = new RequestProcessor();

		SystemSettings.setFlag(AssetsManager.UseAssetSandBox, false);
		SystemSettings.setFlag(AssetsManager.ReportUnusedAssets, false);
		SystemSettings.setFlag(AssetsManagerFlags.AutoresolveDependencies, true);

		final File root = LocalFileSystem.ApplicationHome();
		L.d("lapp-root", root);
		RedReporterEntryPoint.deployResources(root);
		RedReporterEntryPoint.loadMissingJars();

		final DBConfig config = DB.newDBConfig();

		{

			ReporterServer.installComponent("com.jfixby.redreporter.server.RedReporterServer");

			config.setConnectionParametersProvider(new DBConnectionParams());
			config.setUseSSL(false);
			config.setMaxReconnects(1);

			final DataBase mySQL = DB.newDB(config);

			final ServerCoreConfig coreConfig = ReporterServer.newReporterServerConfig();
			coreConfig.setDataBase(mySQL);
			coreConfig.setS3BucketName(System.getenv("S3_BUCKET_NAME"));
			if (coreConfig.getBucketName() == null) {
				coreConfig.setS3BucketName("com.red-triplane.rr-1");
			}

			ReporterServer.deployCore(coreConfig);

		}

	}

	private static void deployResources (final File root) {
		final File cache = root.child("cache");
		try {

			ResourcesManager.installComponent("com.jfixby.red.triplane.resources.fsbased.RedResourcesManager");
			AssetsManager.installComponent("com.jfixby.red.engine.core.resources.RedAssetsManager");

			final ClassLoader classLoader = RedReporterEntryPoint.class.getClassLoader();
			ResourcesManager.registerPackageReader(new RanaJarLoader(classLoader));

			final ResourcesManagerComponent res_manager = ResourcesManager.invoke();

			final File home = LocalFileSystem.ApplicationHome();
			final File assets_folder = home.child("assets");

			if (assets_folder.exists() && assets_folder.isFolder()) {
				final Collection<ResourcesGroup> locals = res_manager.findAndInstallResources(assets_folder);
// locals.print("locals");
				for (final ResourcesGroup local : locals) {
					local.rebuildAllIndexes(null);
				}

			}

			final File assets_cache_folder = home.child("assets-cache");
			{
				final List<String> tanks = Collections.newList("tank-0");
				final HttpURL bankURL = Http.newURL("https://s3.eu-central-1.amazonaws.com/com.red-triplane.assets/bank-lib");
				final ResourcesGroup bank = res_manager.installRemoteBank(bankURL, assets_cache_folder, tanks);
				bank.rebuildAllIndexes(null);
			}

		} catch (final IOException e) {
			e.printStackTrace();
		}

	}

	public static String getHealthReport (final HealthReportType latest, final RedReporterEntryPointArguments arg) {
		return RedReporterEntryPoint.processor.getHealthReport(latest, arg);
	}

	private static void loadMissingJars () {

		try {
			final Class klass = com.fasterxml.jackson.databind.ObjectMapper.class;
			final java.net.URL location = klass.getResource('/' + klass.getName().replace('.', '/') + ".class");
			L.d("XXX klass: " + klass, "loaded from " + location + " by " + klass.getClassLoader());
		} catch (final Throwable e) {
			e.printStackTrace();
			L.d("klass: ", "not found");
		}

		final List<ID> dependencies = Collections.newList();
		dependencies.add(Names.newID("com.mysql.jdbc.jdbc2.optional.MysqlDataSource"));
		dependencies.add(Names.newID("com.amazonaws.services.s3.AmazonS3Client"));
		dependencies.add(Names.newID("org.apache.commons.logging.LogFactory"));
		dependencies.add(Names.newID("org.apache.http.protocol.HttpRequestExecutor"));
		dependencies.add(Names.newID("org.apache.http.client.HttpClient"));
		dependencies.add(Names.newID("org.joda.time.DateTimeZone"));
		dependencies.add(Names.newID("com.amazonaws.partitions.PartitionsLoader"));
		dependencies.add(Names.newID("com.fasterxml.jackson.databind.ObjectMapper"));
		AssetsManager.autoResolveAssets(dependencies, PackageReaderListener.DEFAULT);
	}

	public static final void main (final String[] arg) {

	}

	@Override
	protected void doGet (final HttpServletRequest request, final HttpServletResponse response)
		throws ServletException, IOException {
		RedReporterEntryPoint.processor.processRequest(request, response);
	}

	@Override
	protected void doPost (final HttpServletRequest request, final HttpServletResponse response)
		throws ServletException, IOException {
		RedReporterEntryPoint.processor.processRequest(request, response);
	}

}
