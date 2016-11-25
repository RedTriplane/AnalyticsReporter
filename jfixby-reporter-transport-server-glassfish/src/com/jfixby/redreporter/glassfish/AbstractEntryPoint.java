/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jfixby.redreporter.glassfish;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.jfixby.cmns.adopted.gdx.json.RedJson;
import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.json.Json;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.db.mysql.MySQL;
import com.jfixby.cmns.db.mysql.MySQLConfig;
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

	static {
		DesktopSetup.deploy();
		Json.installComponent(new RedJson());

		final MySQLConfig config = new MySQLConfig();

		config.setServerName(CONFIG.LOCALHOST);
		config.setLogin(CONFIG.DB_LOGIN);
		config.setPassword(CONFIG.DB_PASSWORD);
		config.setDBName(CONFIG.DB_NAME);
		config.setUseSSL(!true);

		final MySQL mySQL = new MySQL(config);

		final RedReporterDataBank bank = new RedReporterDataBank(mySQL);

		final RedReporterServerConfig server_config = new RedReporterServerConfig();
		server_config.setRedReporterDataBank(bank);

		ReporterServer.installComponent(new RedReporterServer(server_config));
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

		final HttpSession session = request.getSession();
		final String session_id = session.getId();

		final ServletInputStream client_to_server_stream = request.getInputStream();
		final ServletOutputStream server_to_client_stream = response.getOutputStream();
		final HashMap<String, String> client_to_server_headers = new HashMap<>();
		final Enumeration<String> header_names = request.getHeaderNames();

		while (header_names.hasMoreElements()) {
			final String key = header_names.nextElement();
			final String value = request.getHeader(key);
			client_to_server_headers.put(key, value);
		}
		client_to_server_headers.put("reqUrl", reqUrl);
		client_to_server_headers.put("path_info", path_info);

		final Map<String, String[]> param_map = request.getParameterMap();
		final Iterator<String> iterator = param_map.keySet().iterator();
		while (iterator.hasNext()) {
			final String key = iterator.next();
			final String[] values = param_map.get(key);
			String value = null;
			if (values != null && values.length > 0) {
				value = values[0];
			}
			client_to_server_headers.put(key.toLowerCase(), value);
		}
		final HashMap<String, String> server_to_client_headers = new HashMap<>();
		this.processRequest(session_id, client_to_server_stream, client_to_server_headers, server_to_client_stream,
			server_to_client_headers);

		final Iterator<String> i = server_to_client_headers.keySet().iterator();
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
		final HashMap<String, String> client_to_server_headers, final ServletOutputStream server_to_client_stream,
		final HashMap<String, String> server_to_client_headers);

	public static PROTOCOL_POLICY PROTOCOL_POLICY () {
		return Debug.checkNull("PROTOCOL_POLICY", http_mode);
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
