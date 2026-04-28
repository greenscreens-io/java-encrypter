/*
 * Copyright (C) 2015, 2022  Green Screens Ltd.
 */
package io.greenscreens.client;

import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.function.Function;
import java.util.regex.Pattern;

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
	public static final Collection<String> HEADERS_TO_TRY = List.of(
			"X-Forwarded-For", " X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP",
			"HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP",
			"HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR" 
	);

	/**
	 * Check if IP address is in valid form
	 * @param ip
	 * @return
	 */
	private static boolean isIPv4Valid(final String ip) {
		return pattern.matcher(ip).matches();
	}

	/**
	 * Search for IP address in string
	 * @param ip
	 * @return
	 */
	private static String detectIP(final String ip) {

		String tmp = null;
		boolean found = false;

		final StringTokenizer tokenizer = new StringTokenizer(Utils.normalize(ip), ",");
		
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
		
		return "unknown".equalsIgnoreCase(tmp) ? null : tmp;
	}

	/**
	 * Get IP address from HttpRequest headers
	 * 
	 * final String ipAddress = IpUtils.findClientIP(req.getRemoteAddr(), v -> req.getHeader(v));
	 * 
	 * @param defaultIP - default address from HttpRequest
	 * @param provider - provide requested header value
	 * @return
	 */
	public static String findClientIP(final String defaultIP, final Function<String, String> provider) {
		return IpUtils.HEADERS_TO_TRY.stream().map(provider)
				.map(v -> detectIP(v))
				.filter(v -> Utils.nonEmpty(v))
				.findFirst()
				.orElse(defaultIP);
	}

}
