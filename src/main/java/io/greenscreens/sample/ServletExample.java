/*
 * Copyright (C) 2015 - 2022 Green Screens Ltd.
 */
package io.greenscreens.sample;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.greenscreens.client.Builder;
import io.greenscreens.client.IpUtils;

/**
 * Servlet example to generate Web Terminal URL 
 */
public class ServletExample extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	// Green Screens Server URL
	private static final String URL  = "http://localhost:9080/";

	/**
	 * Use fingerprint.js inside browser to generate browser id
	 * This is mandatory only if URL sharing is enabled 
	 * http://localhost:9080/ServletExample?fp=12342343
	 */
	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		
		final String ipAddress = IpUtils.getClientIpAddress(req);		
		final long appID = getFingerprint(req);
			
		try {
			final Builder builder = Builder.get(URL, appID, null, null);
			builder.setUUID("2").setHost("DEMO");
			builder.setUser("QSECOFR").setPassword("QSECOFR");
			builder.setIpAddress(ipAddress);
			
			final URI uri = builder.build();
			
			resp.setContentType("text/plain");
			
			final PrintWriter out = resp.getWriter();			
			out.print(uri.toString());
			out.flush();
			
		} catch (Exception e) {
			e.printStackTrace();
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
				
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {		
		doGet(req, resp);
	}

	/**
	 * Get client browser fingerprint calculated with fingerprint.js
	 * @param req
	 * @return
	 */
	protected long getFingerprint(final HttpServletRequest req) {
		
		final String fingerprint = req.getParameter("fp");
		long appID = 0;
		
		try {
			if (nonEmpty(fingerprint)) {
				appID = Long.parseLong(fingerprint);
			}
		} catch (NumberFormatException e ) {
			e.printStackTrace();
		}

		return appID;
				
	}

	/**
	 * Is string value non-empty
	 * @param value
	 * @return
	 */
	protected boolean nonEmpty(final String value) {
		return !isEmpty(value);
	}

	/**
	 * Is string value empty
	 * @param value
	 * @return
	 */
	protected boolean isEmpty(final String value) {
		return normalize(value).length() == 0;
	}

	/**
	 * Normalize string to non null value
	 * @param value
	 * @return
	 */
	protected String normalize(final String value) {
		if (value == null) return "";
		return value.trim();
	}
}
