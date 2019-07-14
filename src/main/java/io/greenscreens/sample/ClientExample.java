/*
 * Copyright (C) 2015 - 2018 Green Screens Ltd.
 */
package io.greenscreens.sample;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import io.greenscreens.client.Builder;

public class ClientExample {

	public static final String URL  = "http://localhost/";
	public static final String SSL_URL = "https://localhost:9443/";
	
	public static void main(String[] args) throws Exception {
		testWithApiKey();
	}

	/**
	 * If username / passwords used, security is based on password timeout,
	 * Additionally, use fixed display name and autoincrement disabled in admin console 
	 * to prevent reuse generated URL  
	 * @throws Exception
	 */
	public static void testWithOtp() throws Exception {

		final String otpKey = "WWLWGFYJIF7RMXAY";
		Builder builder = Builder.get(URL, null, otpKey);
		builder.setUUID("0").setHost("DEMO");
		builder.setUser("QSECOFR").setPassword("QSECOFR");	
		builder.setExpiration(30, TimeUnit.SECONDS);
		
		URI uri = builder.build();
		System.out.println(uri.toString());

	}

	/**
	 * Generate URl with access based on ApiKey.  
	 * OTP is ignored, password will not expire
	 * Access is checked by client IP address and IP registered with API Key
	 * @throws Exception
	 */
	public static void testWithApiKey() throws Exception {

		final String apiKey = "1a4e39c8-07d9-4a2b-b021-d1f4103d1a22";
		Builder builder = Builder.get(URL, apiKey, null);
		builder.setUUID("0").setHost("DEMO");
		builder.setUser("QSECOFR").setPassword("QSECOFR");	
		builder.setExpiration(30, TimeUnit.SECONDS);
		
		URI uri = builder.build();
		System.out.println(uri.toString());

	}
	
	/**
	 * If username / passwords used, security is based on password timeout,
	 * Additionally, use fixed display name and autoincrement disabled in admin console 
	 * to prevent reuse generated URL  
	 * @throws Exception
	 */
	public static void testWithUrlSharingDisabled() throws Exception {

		Builder builder = Builder.get(URL, null, null);
		builder.setUUID("0").setHost("DEMO");
		builder.setUser("QSECOFR").setPassword("QSECOFR");	
		builder.setExpiration(30, TimeUnit.SECONDS);
		
		URI uri = builder.build();
		System.out.println(uri.toString());

	}
	
	/**
	 * Best security option, if used, browser must send browser fingerprint, 
	 * then use it to generate access url. No other browser will be able to reuse generated URL   
	 * @throws Exception
	 */
	public static void testWithUrlSharingEnabled() throws Exception {

		// fingerprint or mobile app id 
		long appID = 1234567890;
		
		Builder builder = Builder.get(SSL_URL, appID, null, null);
		builder.setUUID("0").setHost("DEMO");
		builder.setUser("QSECOFR").setPassword("QSECOFR");
		
		URI uri = builder.build();
		System.out.println(uri.toString());
	}

}
