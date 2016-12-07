
package com.jfixby.redreporter.glassfish;

import com.jfixby.cmns.api.desktop.DesktopSetup;
import com.jfixby.cmns.api.json.Json;
import com.jfixby.cmns.api.sys.settings.SystemSettings;
import com.jfixby.cmns.aws.api.AWS;
import com.jfixby.cmns.db.api.ConnectionParametersProvider;
import com.jfixby.cmns.db.api.DB;
import com.jfixby.cmns.db.api.DBConfig;
import com.jfixby.cmns.db.api.DataBase;
import com.jfixby.cmns.ver.Version;
import com.jfixby.redreporter.server.api.ReporterServer;
import com.jfixby.redreporter.server.api.ServerCoreConfig;

public class ServerDeployer {
	static ConnectionParametersProvider connectionParamatesProvider = new ConnectionParametersProvider() {
		@Override
		public String getHost () {
			return System.getenv("RDS_HOSTNAME");
		}

		@Override
		public int getPort () {
			final String port = System.getenv("RDS_PORT");
			if (port == null) {
				return -1;
			}
			if ("".equals(port)) {
				return -1;
			}
			return Integer.parseInt(port);
		}

		@Override
		public String getLogin () {
			return System.getenv("RDS_USERNAME");
		}

		@Override
		public String getPassword () {
			return System.getenv("RDS_PASSWORD");
		}

		@Override
		public String getDBName () {
			return System.getenv("RDS_DB_NAME");
		}

	};

	public static void deploy (final Version version) {

		DesktopSetup.deploy();
		Json.installComponent("com.jfixby.cmns.adopted.gdx.json.RedJson");
		DB.installComponent("com.jfixby.cmns.db.mysql.MySQLDB");
		AWS.installComponent("com.jfixby.amazon.aws.RedAWS");

		SystemSettings.setStringParameter(Version.Tags.PackageName, version.packageName);
		SystemSettings.setStringParameter(Version.Tags.VersionCode, version.versionCode + "");
		SystemSettings.setStringParameter(Version.Tags.VersionName, version.getPackageVersionString());

		final DBConfig config = DB.newDBConfig();

		{

			ReporterServer.installComponent("com.jfixby.redreporter.server.RedReporterServer");

			config.setConnectionParametersProvider(connectionParamatesProvider);
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

	public static final void main (final String[] arg) {
		deploy(RedReporterEntryPoint.version);
	}

}
