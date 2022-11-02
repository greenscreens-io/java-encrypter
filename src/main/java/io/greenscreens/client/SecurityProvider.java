/*
 * Copyright (C) 2015, 2016  Green Screens Ltd.
 */
package io.greenscreens.client;

import java.security.Provider;
import java.security.Security;
import java.util.Objects;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public enum SecurityProvider {
	;

	public static final String PROVIDER_NAME = BouncyCastleProvider.PROVIDER_NAME;
	
	private static Provider provider;

	static {
		initialize();
	}

	public static void initialize() {
		if (Objects.nonNull(provider)) return;
		provider = getProvider();
	}
	
	private static Provider getProvider() {
		
		Provider provider = null;
		final Provider [] providers = Security.getProviders();
		if (providers.length > 0) {
			if (providers[0].getName().equals(PROVIDER_NAME)) {
				provider = providers[0];
				return provider;
			}
		}
		
		provider = new BouncyCastleProvider();
		Security.removeProvider(PROVIDER_NAME);
		Security.insertProviderAt(provider, 1);
		
		return provider;
	}
	
	public static Provider get() {
		return provider;
	}
	
}
