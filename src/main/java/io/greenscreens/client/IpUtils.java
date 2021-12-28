/*
 * Copyright (C) 2015 - 2022 Green Screens Ltd.
 */
package io.greenscreens.client;

import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

/**
 * Utility for handling and parsing IP address
 */
public enum IpUtils {
	;

	private static final String _255 = "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";	
	private static final Pattern pattern = Pattern.compile("^(?:" + _255 + "\\.){3}" + _255 + "$");
	
	/**
	 * HTTP headers that might contain IP address
	 */
	private static final String[] HEADERS_TO_TRY = { 
			"X-Forwarded-For", " X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP",
			"HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP",
			"HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR" 
	};

	/**
	 * Check if IP address is in valid form
	 * @param ip
	 * @return
	 */
	private static boolean isIPv4Valid(String ip) {
		return pattern.matcher(ip).matches();
	}

	/**
	 * Search for IP address in string
	 * @param ip
	 * @return
	 */
	private static String detectIP(String ip) {

		String tmp = null;
		boolean found = false;

		final StringTokenizer tokenizer = new StringTokenizer(ip, ",");
		
		while (tokenizer.hasMoreTokens()) {
		
			tmp = tokenizer.nextToken().trim();
			
			if (isIPv4Valid(tmp)) {
				found = true;
				break;
			}
			
		}

		if (!found) {
			tmp = null;
		}
		
		return tmp;
	}


	/**
	 * Get IP address from HttpRequest
	 * @param request
	 * @return
	 */
	public static String getClientIpAddress(HttpServletRequest request) {
		
		String ip = null;
		
		for (String header : HEADERS_TO_TRY) {
		
			ip = request.getHeader(header);
			
			if (ip!= null) {
				ip = detectIP(ip);
			}
			
			if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
				return ip;
			
			}
		}
		
		return request.getRemoteAddr();
	}

}
