/*
 * Copyright (C) 2015 - 2018 Green Screens Ltd.
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
	
	private static final String URL  = "http://localhost:9080/";

	/**
	 * Use fingerprint.js inside browser to generate browser id
	 * This is mandatory only if URL sharing is enabled 
	 * http://localhost:9080/ServletExample?fp=12342343
	 */
	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		
		final String fingerprint = req.getParameter("fp");
		final String ipAddress = IpUtils.getClientIpAddress(req);
		
		long appID = 0;
		try {
			if (fingerprint != null) {
				appID = Long.parseLong(fingerprint);
			}
		} catch (NumberFormatException e ) {
			e.printStackTrace();
		}
			
		try {
			Builder builder = Builder.get(URL, appID, null, null);
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

}
