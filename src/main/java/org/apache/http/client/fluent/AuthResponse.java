/*
 * Copyright (C) 2015 - 2022 Green Screens Ltd.
 */
package org.apache.http.client.fluent;

import org.apache.http.HttpResponse;

/**
 * Helper apache http client fluent API
 * to allow access to constructor 
 */
public class AuthResponse extends Response {

	public AuthResponse(HttpResponse response) {
		super(response);

	}
	
}
