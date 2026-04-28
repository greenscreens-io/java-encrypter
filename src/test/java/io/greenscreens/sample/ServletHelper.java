package io.greenscreens.sample;

import java.io.PrintWriter;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.greenscreens.client.Builder;
import io.greenscreens.client.Utils;

enum ServletHelper {
	;
	
	private static final Logger LOG = LoggerFactory.getLogger(ServletHelper.class);
	
	// Green Screens Server URL
	private static final String URL  = "http://localhost:9080/";

	static void write(final PrintWriter out, final URI uri) throws Exception {
		out.print(uri.toString());
		out.flush();		
	}

	static URI toURI(final String clientIP, final long appID) throws Exception {
		final Builder builder = Builder.get(URL, appID, null, null);
		builder.setUUID("DEMO").setHost("DEMO");
		builder.setUser("QSECOFR").setPassword("QSECOFR");
		builder.setIpAddress(clientIP);		
		return builder.build();
	}
	
	/**
	 * Get client browser fingerprint calculated with fingerprint.js
	 * @param req
	 * @return
	 */
	static long getFingerprint(final String fingerprint) {
		
		long appID = 0;
		
		try {
			if (Utils.nonEmpty(fingerprint)) {
				appID = Math.abs(Long.parseLong(fingerprint));
			}
		} catch (NumberFormatException e ) {
			LOG.error(e.getMessage());
			LOG.debug(e.getMessage(), e);
		}

		return appID;
				
	}
	

}
