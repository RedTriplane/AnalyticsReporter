/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jfixby.redreporter.glassfish;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.jfixby.amazon.aws.s3.S3CredentialsProvider;
import com.jfixby.cmns.adopted.gdx.json.RedJson;
import com.jfixby.cmns.api.assets.Names;
import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.List;
import com.jfixby.cmns.api.collections.Map;
import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.file.LocalFileSystem;
import com.jfixby.cmns.api.floatn.Float2;
import com.jfixby.cmns.api.geometry.Geometry;
import com.jfixby.cmns.api.io.Buffer;
import com.jfixby.cmns.api.io.BufferInputStream;
import com.jfixby.cmns.api.io.GZipInputStream;
import com.jfixby.cmns.api.io.IO;
import com.jfixby.cmns.api.io.InputStream;
import com.jfixby.cmns.api.io.OutputStream;
import com.jfixby.cmns.api.java.ByteArray;
import com.jfixby.cmns.api.java.Int;
import com.jfixby.cmns.api.java.gc.GCFisher;
import com.jfixby.cmns.api.java.gc.MemoryStatistics;
import com.jfixby.cmns.api.json.Json;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.math.Average;
import com.jfixby.cmns.api.math.FloatMath;
import com.jfixby.cmns.api.net.http.Http;
import com.jfixby.cmns.api.net.http.HttpConnection;
import com.jfixby.cmns.api.net.http.HttpConnectionInputStream;
import com.jfixby.cmns.api.net.http.HttpURL;
import com.jfixby.cmns.api.net.message.Message;
import com.jfixby.cmns.api.sys.SystemInfoTags;
import com.jfixby.cmns.api.sys.settings.SystemSettings;
import com.jfixby.cmns.api.taskman.TASK_TYPE;
import com.jfixby.cmns.api.util.JUtils;
import com.jfixby.cmns.db.mysql.ConnectionParametersProvider;
import com.jfixby.cmns.db.mysql.MySQL;
import com.jfixby.cmns.db.mysql.MySQLConfig;
import com.jfixby.cmns.ver.Version;
import com.jfixby.red.desktop.DesktopSetup;
import com.jfixby.red.filesystem.virtual.InMemoryFileSystem;
import com.jfixby.redreporter.api.ServerStatus;
import com.jfixby.redreporter.api.transport.REPORTER_PROTOCOL;
import com.jfixby.redreporter.client.http.ReporterHttpClientConfig;
import com.jfixby.redreporter.server.api.ReporterServer;
import com.jfixby.redreporter.server.core.RedReporterDataBank;
import com.jfixby.redreporter.server.core.RedReporterServer;
import com.jfixby.redreporter.server.core.RedReporterServerConfig;
import com.jfixby.redreporter.server.core.file.FileStorage;
import com.jfixby.redreporter.server.core.file.FileStorageConfig;
import com.jfixby.redreporter.server.credentials.CONFIG;

public abstract class RedReporterEntryPoint extends HttpServlet {

	/**
	 *
	 */

	private static final long serialVersionUID = -1649148797847741708L;
	private static PROTOCOL_POLICY http_mode = PROTOCOL_POLICY.ALLOW_BOTH;
	private static MySQL mySQL;
	public static Version version;
	private static RedReporterDataBank bank;
	public static String instance_id;
	static int MAX_VALUES = 500;
	static final Average average;

	final static synchronized private void addValueToAverage (final double val, final Float2 value, final Int size) {
		average.addValue(val);
		if (size != null) {
			size.value = average.size();
		}
		if (value != null) {
			value.setX(average.getAverage());
		}
	}

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

	};

	static S3CredentialsProvider s3CredentialsProvider = new S3CredentialsProvider() {
		@Override
		public String getAccessKeyID () {
			return System.getenv("S3_ACCESS_KEY_ID");
		}

		@Override
		public String getSecretKeyID () {
			return System.getenv("S3_SECRET_KEY_ID");
		}
	};

	private static ServerStatus lastServiceState;
	private static FileStorage fileStorage;

	static {
		DesktopSetup.deploy();
		Json.installComponent(new RedJson());
		average = FloatMath.newAverage(MAX_VALUES);
		version = new Version();
		version.major = "1";
		version.minor = "30";
		version.build = "0";
		version.packageName = "com.jfixby.redreporter.glassfish";
		version.versionCode = 715;

		SystemSettings.setStringParameter(Version.Tags.PackageName, version.packageName);
		SystemSettings.setStringParameter(Version.Tags.VersionCode, version.versionCode + "");
		SystemSettings.setStringParameter(Version.Tags.VersionName, version.getPackageVersionString());
		INSTALLATION_ID_FILE_NAME = version.packageName + ".iid";
// deployAnalytics();

		final MySQLConfig config = new MySQLConfig();

		{
// config.setServerName(hostname);
// config.setLogin(userName);
// config.setPort(port);
// config.setPassword(password);

			config.setConnectionParametersProvider(connectionParamatesProvider);
			config.setDBName(CONFIG.DB_NAME);
			config.setUseSSL(false);
			config.setMaxReconnects(1);

			mySQL = new MySQL(config);

			bank = new RedReporterDataBank(mySQL);

			final FileStorageConfig fsConfig = new FileStorageConfig();
			fsConfig.setS3CredentialsProvider(s3CredentialsProvider);
			fsConfig.setBucketName(CONFIG.S3_BUCKET_NAME);

			fileStorage = new FileStorage(fsConfig);

			final RedReporterServerConfig server_config = new RedReporterServerConfig();
			server_config.setReportsFileStorage(fileStorage);
			server_config.setRedReporterDataBank(bank);
			instance_id = red_instance_id();
			ReporterServer.installComponent(new RedReporterServer(server_config));
		}
	}
	private static final String INSTALLATION_ID_FILE_NAME;

	static public void deployAnalytics () {
		{
			L.d("INSTALLATION_ID_FILE_NAME", INSTALLATION_ID_FILE_NAME);
			final File home = LocalFileSystem.ApplicationHome();
			final File logs = setupLogFolder(home);

			final ReporterHttpClientConfig transport_config = new ReporterHttpClientConfig();

			transport_config.setInstallationIDStorageFolder(home);
			transport_config.setCacheFolder(logs);
			transport_config.setIIDFileName(INSTALLATION_ID_FILE_NAME);
			transport_config.setTaskType(TASK_TYPE.SEPARATED_THREAD);
			{
				final String url_string = "https://rr-0.red-triplane.com/";
				final HttpURL url = Http.newURL(url_string);
				transport_config.addAnalyticsServerUrl(url);
			}
			{
				final String url_string = "https://rr-1.red-triplane.com/";
				final HttpURL url = Http.newURL(url_string);
				transport_config.addAnalyticsServerUrl(url);
			}
			{
				final String url_string = "https://rr-2.red-triplane.com/";
				final HttpURL url = Http.newURL(url_string);
				transport_config.addAnalyticsServerUrl(url);
			}
// final ReporterTransport transport = new ReporterHttpClient(transport_config);
			{
// CrashReporter.installComponent(new RedCrashReporter(transport));
// CrashReporter.enableErrorsListener();
// CrashReporter.enableLogsListener();
// CrashReporter.enableUncaughtExceptionHandler();
			}
			{
// AnalyticsReporter.installComponent(new RedAnalyticsReporter(transport));
// AnalyticsReporter.reportStart();
			}
		}
	}

	final private static File setupLogFolder (final File home) {
		File logs = null;
		try {
			logs = home.child("logs");
			logs.makeFolder();
			if (logs.isFolder()) {
				return logs;
			}
		} catch (final IOException e) {
			L.e(e);
		}
		final InMemoryFileSystem imfs = new InMemoryFileSystem();
		return imfs.ROOT();
	}

	public static final String serviceState () {
		lastServiceState = ReporterServer.getStatus();
		return "[" + lastServiceState + "]";
	}

	static private final String red_instance_id () {
		String instance_id;
		try {
			final String url_string = "http://169.254.169.254/latest/meta-data/instance-id";
			final HttpURL url = Http.newURL(url_string);
			final HttpConnection connect = Http.newConnection(url);
			connect.open();
			final HttpConnectionInputStream is = connect.getInputStream();
			is.open();
			final ByteArray data = is.readAll();
			is.close();
			connect.close();
			instance_id = JUtils.newString(data);
		} catch (final Exception e) {
			L.e("failed to get instance id", e);
			instance_id = "no_instance_id-" + System.currentTimeMillis();
		}
		return instance_id;
	}

	/** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs */

	protected void processRequest (final HttpServletRequest request, final HttpServletResponse response) {
		final RedReporterEntryPointArguments arg = new RedReporterEntryPointArguments();
		String client_ip_addr = "unknown";
		try {
			final boolean https = check_https(request);

			if (PROTOCOL_POLICY() == PROTOCOL_POLICY.HTTPS_ONLY && !https) {
				force_https(request, response);
				return;
			}
			if (PROTOCOL_POLICY() == PROTOCOL_POLICY.HTTP_ONLY && https) {
				force_http(request, response);
				return;
			}

			String path_info = request.getPathInfo();
			if (path_info == null) {
				path_info = "";
			}

			final String reqUrl = request.getRequestURL() + "";
			if (reqUrl.toLowerCase().endsWith("favicon.ico")) {
				return;
			}
			if (reqUrl.toLowerCase().endsWith("health")) {
				arg.isHeathCheck = true;
			}

			arg.timestamp = System.currentTimeMillis();
			arg.request_number = RedReporterEntryPoint.request_number();
			final HttpSession session = request.getSession();
			final String session_id = session.getId();
			arg.requestID = Names.ROOT().child("iid-" + instance_id).child("sid-" + session_id).child("rqn-" + arg.request_number);

			L.d("----Request[" + arg.requestID + "]----------------------------------------------");

			final ServletInputStream client_to_server_stream = request.getInputStream();
			final ServletOutputStream server_to_client_stream = response.getOutputStream();
			final Map<String, List<String>> client_to_server_headers = Collections.newMap();
			final Enumeration<String> header_names = request.getHeaderNames();

			while (header_names.hasMoreElements()) {
				final String key = header_names.nextElement();
				final String value = request.getHeader(key);
				client_to_server_headers.put(key, Collections.newList(value));
			}
			client_to_server_headers.put("reqUrl", Collections.newList(reqUrl));
			client_to_server_headers.put("path_info", Collections.newList(path_info));
			client_ip_addr = getClientIpAddr(request);
			client_to_server_headers.put(SystemInfoTags.Net.client_ip, Collections.newList(client_ip_addr));

			final java.util.Map<String, String[]> param_map = request.getParameterMap();
			final Iterator<String> iterator = param_map.keySet().iterator();
			while (iterator.hasNext()) {
				final String key = iterator.next();
				final String[] values = param_map.get(key);
				final List<String> list = Collections.newList(values);
				client_to_server_headers.put(key.toLowerCase(), list);
			}
			final Map<String, String> server_to_client_headers = Collections.newMap();

			arg.inputHeaders = client_to_server_headers;
			arg.server_to_client_stream = server_to_client_stream;
			arg.client_to_server_stream = client_to_server_stream;
// arg.print();
			processRequest(arg);

			final Iterator<String> i = server_to_client_headers.keys().iterator();
			final String new_location = server_to_client_headers.get("WEB_SERVER.REDIRECT");
			if (new_location != null) {
				response.sendRedirect(new_location);
				L.d("redirect: " + new_location);
			} else {
				while (i.hasNext()) {
					final String key = i.next();
					final String value = server_to_client_headers.get(key);
					response.setHeader(key, value);
				}
			}
			server_to_client_stream.flush();
			server_to_client_stream.close();

		} catch (final Throwable e) {
			L.e("failed request " + arg.requestID, e);

		}
		final long processed_in = System.currentTimeMillis() - arg.timestamp;
		L.d("request", arg.requestID);
		L.d("processed in", processed_in + " ms");
		L.d("          ip", client_ip_addr);
		final MemoryStatistics memoryStats = GCFisher.getMemoryStatistics();
		L.d("memory usage", memoryStats);
	}

	/**
	 *
	 */
	private static final long MAX_BYTES_TO_READ = 1024 * 100;
	static long request = 0;

	static void processRequest (final RedReporterEntryPointArguments arg) {
		try {
			final String len = MessageProcessor.getHeader("content-length", arg.inputHeaders);
			if (arg.isHeathCheck) {
				sayHello(arg);
				return;
			}
			if (len == null) {
				sayHello(arg);
				return;
			}
			if ("0".equals(len)) {
				sayHello(arg);
				return;
			}
			final InputStream is = IO.newInputStream( () -> arg.client_to_server_stream);
			is.open();

			final ByteArray bytes = IO.readMax(is, MAX_BYTES_TO_READ);
			final ByteArray data;
			{
				final Buffer buffer = IO.newBuffer(bytes);
				final BufferInputStream bis = IO.newBufferInputStream(buffer);
				bis.open();
				{
					final GZipInputStream gzip = IO.newGZipStream(bis);
					gzip.open();
					data = IO.readMax(gzip, MAX_BYTES_TO_READ);
					gzip.close();
				}
				bis.close();
			}

			arg.message = IO.deserialize(Message.class, data);
			is.close();

			Message answer = MessageProcessor.process(arg);
			if (answer == null) {
				answer = new Message(REPORTER_PROTOCOL.ERR);
			}
			final Long processingTime = System.currentTimeMillis() - arg.timestamp;
			answer.attachments.put(REPORTER_PROTOCOL.SERVER_RESPONDED_IN, processingTime);
			arg.message.values.put(REPORTER_PROTOCOL.SERVER_CODE_VERSION, "" + version.getVersionString());
			arg.message.values.put(REPORTER_PROTOCOL.REQUEST_ID, arg.requestID + "");
			final OutputStream os = IO.newOutputStream( () -> arg.server_to_client_stream);
			os.open();

			final ByteArray responceBytes = IO.serialize(answer);
			final ByteArray compressedResponse = IO.compress(responceBytes);
			os.write(compressedResponse);
			os.close();
		} catch (final Throwable e) {
			L.e("failed request " + arg.requestID, e);
		}
// outputHeaders.print("outputHeaders");

		addValueToAverage(measureProcessingTime(arg), null, null);
	}

	private static synchronized long request_number () {
		request++;
		return request;
	}

	public final static String SEPARATOR = System.getProperty("line.separator");

	static private void sayHello (final RedReporterEntryPointArguments arg) throws IOException {
		final StringBuilder msg = new StringBuilder();
		if (arg.isHeathCheck) {
			msg.append("        Health check: " + "[" + lastServiceState + "]").append(SEPARATOR);
		} else {
			msg.append("Service is operating: " + serviceState()).append(SEPARATOR);
		}

		final double val = measureProcessingTime(arg);
		final Float2 value = Geometry.newFloat2();
		final Int size = new Int();
		addValueToAverage(val, value, size);
		final double sec = FloatMath.roundToDigit(val, 3);
		msg.append("         server time: " + new Date()).append(SEPARATOR);
		final MemoryStatistics memoryStats = GCFisher.getMemoryStatistics();
		msg.append("             version: " + version.getPackageVersionString()).append(SEPARATOR);
		msg.append(SEPARATOR);
		msg.append("        memory usage: " + memoryStats).append(SEPARATOR);
		msg.append(SEPARATOR);
		msg.append("           client ip: " + arg.inputHeaders.get(SystemInfoTags.Net.client_ip)).append(SEPARATOR);
		msg.append("          request id: " + arg.requestID).append(SEPARATOR);
		msg.append(SEPARATOR);
		msg.append("request processed in: " + sec + " sec").append(SEPARATOR);
		msg.append("average for the last: " + size.value + " requests").append(SEPARATOR);
		msg.append("                  is: " + FloatMath.roundToDigit(value.getX(), 3) + " sec").append(SEPARATOR);

		arg.server_to_client_stream.write(msg.toString().getBytes());
	}

	static private double measureProcessingTime (final RedReporterEntryPointArguments arg) {
		return (System.currentTimeMillis() - arg.timestamp) / 1000d;
	}

	public static final void main (final String[] arg) {

	}

	public static PROTOCOL_POLICY PROTOCOL_POLICY () {
		return Debug.checkNull("PROTOCOL_POLICY", http_mode);
	}

	public static String getClientIpAddr (final HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_FORWARDED");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_VIA");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("REMOTE_ADDR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		L.d("ip", "<" + ip + ">");
		return ip;
	}

	// <editor-fold defaultstate="collapsed"
	// desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
	/** Handles the HTTP <code>GET</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs */
	@Override
	protected void doGet (final HttpServletRequest request, final HttpServletResponse response)
		throws ServletException, IOException {
		this.processRequest(request, response);
	}

	/** Handles the HTTP <code>POST</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs */
	@Override
	protected void doPost (final HttpServletRequest request, final HttpServletResponse response)
		throws ServletException, IOException {
		this.processRequest(request, response);
	}

	/** Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description */
	@Override
	public String getServletInfo () {
		return "Short description";
	}// </editor-fold>

	static private boolean check_https (final HttpServletRequest request) {
		return request.isSecure();
	}

	static private void force_https (final HttpServletRequest request, final HttpServletResponse res) throws IOException {
		final String protocol = request.getScheme();

		String reqUrl = request.getRequestURL().toString().replaceFirst(protocol, "https");

		L.d("Forcing HTTPS: " + reqUrl);
		final String queryString = request.getQueryString();
		if (queryString != null) {
			reqUrl += '?' + queryString;
		}
		res.sendRedirect(reqUrl);
	}

	static private void force_http (final HttpServletRequest request, final HttpServletResponse res) throws IOException {
		final String protocol = request.getScheme();

		String reqUrl = request.getRequestURL().toString().replaceFirst(protocol, "http");

		L.d("Forcing HTTP: " + reqUrl);
		final String queryString = request.getQueryString();
		if (queryString != null) {
			reqUrl += '?' + queryString;
		}
		res.sendRedirect(reqUrl);
	}

}
