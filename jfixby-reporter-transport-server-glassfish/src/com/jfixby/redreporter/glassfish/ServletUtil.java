
package com.jfixby.redreporter.glassfish;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfixby.scarabei.api.log.L;

public class ServletUtil {

	static public void forceHttpS (final HttpServletRequest request, final HttpServletResponse res) throws IOException {
		final String protocol = request.getScheme();

		String reqUrl = request.getRequestURL().toString().replaceFirst(protocol, "https");

		L.d("Forcing HTTPS: " + reqUrl);
		final String queryString = request.getQueryString();
		if (queryString != null) {
			reqUrl += '?' + queryString;
		}
		res.sendRedirect(reqUrl);
	}

	static public void forceHttp (final HttpServletRequest request, final HttpServletResponse res) throws IOException {
		final String protocol = request.getScheme();

		String reqUrl = request.getRequestURL().toString().replaceFirst(protocol, "http");

		L.d("Forcing HTTP: " + reqUrl);
		final String queryString = request.getQueryString();
		if (queryString != null) {
			reqUrl += '?' + queryString;
		}
		res.sendRedirect(reqUrl);
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
}
