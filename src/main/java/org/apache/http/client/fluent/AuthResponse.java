/*
 * Copyright (C) 2015 - 2018 Green Screens Ltd.
 */
package org.apache.http.client.fluent;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Response;

/**
 * Helper apache http client fluent API
 * to allow access to constructor 
 */
public class AuthResponse extends Response {

	public AuthResponse(HttpResponse response) {
		super(response);

	}
	
}
