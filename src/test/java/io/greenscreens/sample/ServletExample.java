/*
 * Copyright (C) 2015 - 2022 Green Screens Ltd.
 */
package io.greenscreens.sample;

import java.io.IOException;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import io.greenscreens.client.IpUtils;

/**
 * Servlet example to generate Web Terminal URL 
 */
public class ServletExample extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(ServletExample.class);
	

	/**
	 * Use fingerprint.js inside browser to generate browser id
	 * This is mandatory only if URL sharing is enabled 
	 * http://localhost:9080/ServletExample?fp=12342343
	 */
	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
			
		try {
			resp.setContentType("text/plain");
			
			final long appID = ServletHelper.getFingerprint(req.getParameter("fp"));
			final String ipAddress = IpUtils.findClientIP(req.getRemoteAddr(), v -> req.getHeader(v));		
			final URI uri = ServletHelper.toURI(ipAddress, appID);			
			ServletHelper.write(resp.getWriter(), uri);
			
		} catch (Exception e) {
			LOG.error(e.getMessage());
			LOG.debug(e.getMessage(), e);
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
				
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {		
		doGet(req, resp);
	}

}
