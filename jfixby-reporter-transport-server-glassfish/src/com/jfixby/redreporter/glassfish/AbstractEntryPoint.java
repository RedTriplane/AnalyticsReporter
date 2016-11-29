/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jfixby.redreporter.glassfish;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.jfixby.cmns.adopted.gdx.json.RedJson;
import com.jfixby.cmns.api.collections.Collections;
import com.jfixby.cmns.api.collections.List;
import com.jfixby.cmns.api.collections.Map;
import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.java.ByteArray;
import com.jfixby.cmns.api.json.Json;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.api.net.http.Http;
import com.jfixby.cmns.api.net.http.HttpConnection;
import com.jfixby.cmns.api.net.http.HttpConnectionInputStream;
import com.jfixby.cmns.api.net.http.HttpURL;
import com.jfixby.cmns.api.sys.SystemInfoTags;
import com.jfixby.cmns.api.util.JUtils;
import com.jfixby.cmns.db.mysql.MySQL;
import com.jfixby.cmns.db.mysql.MySQLConfig;
import com.jfixby.cmns.ver.VERSION_STAGE;
import com.jfixby.cmns.ver.Version;
import com.jfixby.red.desktop.DesktopSetup;
import com.jfixby.redreporter.server.api.ReporterServer;
import com.jfixby.redreporter.server.core.RedReporterDataBank;
import com.jfixby.redreporter.server.core.RedReporterServer;
import com.jfixby.redreporter.server.core.RedReporterServerConfig;
import com.jfixby.redreporter.server.credentials.CONFIG;

public abstract class AbstractEntryPoint extends HttpServlet {

	/**
	 *
	 */

	private static final long serialVersionUID = -1649148797847741708L;
	private static PROTOCOL_POLICY http_mode = PROTOCOL_POLICY.ALLOW_BOTH;
	private static MySQL mySQL;
	public static Version version;
	private static RedReporterDataBank bank;
	public static final String instance_id;

	static {
		DesktopSetup.deploy();
		Json.installComponent(new RedJson());

		version = new Version();
		version.major = 1;
		version.minor = 0;
		version.build = 50;
		version.packageName = "com.jfixby.redreporter.glassfish";
		version.stage = VERSION_STAGE.ALPHA;
		version.versionCode = 0;

		final MySQLConfig config = new MySQLConfig();

		config.setServerName(CONFIG.DB_SERVER);
		config.setLogin(CONFIG.DB_LOGIN);
		config.setPassword(CONFIG.DB_PASSWORD);
		config.setDBName(CONFIG.DB_NAME);
		config.setConnectionDrainTime(30);
		config.setUseSSL(!true);

		mySQL = new MySQL(config);

		bank = new RedReporterDataBank(mySQL);

		final RedReporterServerConfig server_config = new RedReporterServerConfig();
		server_config.setRedReporterDataBank(bank);
		instance_id = red_instance_id();
		ReporterServer.installComponent(new RedReporterServer(server_config));

	}

	public static String serviceState () {
		return "[" + ReporterServer.getState() + "]";
	}

	static private String red_instance_id () {
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
			L.d(e);
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

	protected void processRequest (final HttpServletRequest request, final HttpServletResponse response)
		throws ServletException, IOException {

		final boolean https = this.check_https(request);

		if (PROTOCOL_POLICY() == PROTOCOL_POLICY.HTTPS_ONLY && !https) {
			this.force_https(request, response);
			return;
		}
		if (PROTOCOL_POLICY() == PROTOCOL_POLICY.HTTP_ONLY && https) {
			this.force_http(request, response);
			return;
		}

		String path_info = request.getPathInfo();
		if (path_info == null) {
			path_info = "";
		}

		final String reqUrl = request.getRequestURL() + "";
		if (reqUrl.endsWith("favicon.ico")) {
			return;
		}

		final HttpSession session = request.getSession();
		final String session_id = session.getId();

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
		final String client_ip_addr = getClientIpAddr(request);
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
		this.processRequest(session_id, client_to_server_stream, client_to_server_headers, server_to_client_stream,
			server_to_client_headers);

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
	}

	abstract void processRequest (final String session_id, final ServletInputStream client_to_server_stream,
		final Map<String, List<String>> client_to_server_headers, final ServletOutputStream server_to_client_stream,
		final Map<String, String> server_to_client_headers);

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

	private boolean check_https (final HttpServletRequest request) {
		return request.isSecure();
	}

	private void force_https (final HttpServletRequest request, final HttpServletResponse res) throws IOException {
		final String protocol = request.getScheme();

		String reqUrl = request.getRequestURL().toString().replaceFirst(protocol, "https");

		L.d("Forcing HTTPS: " + reqUrl);
		final String queryString = request.getQueryString();
		if (queryString != null) {
			reqUrl += '?' + queryString;
		}
		res.sendRedirect(reqUrl);
	}

	private void force_http (final HttpServletRequest request, final HttpServletResponse res) throws IOException {
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
