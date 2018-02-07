/*
 * Copyright (C) 2015 - 2018 Green Screens Ltd.
 */
package io.greenscreens.sample;

import java.net.URI;

import io.greenscreens.client.Builder;

public class ClientExample {

	public static final String URL  = "http://localhost:9080/";
	public static final String SSL_URL = "https://localhost:9443/";
	
	public static void main(String[] args) throws Exception {
		testWithUrlSharingDisabled();
	}

	/**
	 * If username / passwords used, security is based on password timeout,
	 * Additionally, use fixed display name and autoincrement disabled in admin console 
	 * to prevent reuse generated URL  
	 * @throws Exception
	 */
	public static void testWithUrlSharingDisabled() throws Exception {

		Builder builder = Builder.get(URL);
		builder.setUUID("0").setHost("DEMO");
		builder.setUser("QSECOFR").setPassword("QSECOFR");
		
		URI uri = builder.build();
		System.out.println(uri.toString());

	}
	
	/**
	 * Best security option, if used, browser must send browser fingerprint, 
	 * then use it to generate access url. No other browser will be able to reuse geenrated URL   
	 * @throws Exception
	 */
	public static void testWithUrlSharingEnabled() throws Exception {

		// fingerprint or mobile app id 
		long appID = 1234567890;
		
		Builder builder = Builder.get(SSL_URL, appID);
		builder.setUUID("0").setHost("DEMO");
		builder.setUser("QSECOFR").setPassword("QSECOFR");
		
		URI uri = builder.build();
		System.out.println(uri.toString());
	}

}
