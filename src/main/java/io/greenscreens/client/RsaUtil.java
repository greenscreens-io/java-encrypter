/*
 * Copyright (C) 2015 - 2018 Green Screens Ltd.
 */
package io.greenscreens.client;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * RSA utility to work with public RSA key
 */
enum RsaUtil {
	;

	private static Log LOG = LogFactory.getLog(RsaUtil.class);
	
	private static final String RSA_MODE = "RSA/ECB/PKCS1Padding";
	
	/**
	 * Get RSA factory instance
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 */
	private static KeyFactory getKeyFactory() throws NoSuchAlgorithmException, NoSuchProviderException {
		return KeyFactory.getInstance("RSA");
	}	
	
	/**
	 * Convert PEM file to decoded byte array
	 * Removes headers and new lines then decode from Base64
	 * @param raw
	 * @return
	 */
	private static byte[] convertFromPEM(final byte[] raw) {
		
		String data = new String(raw);		
		//final String [] lines = data.split("\\r?\\n");
		
		final String [] lines = StringUtils.splitPreserveAllTokens(data.trim(),"\n");
		
		if (lines.length>1) {
			lines[0] = "";
			lines[lines.length-1] = "";
			data = StringUtils.join(lines, "");			
		}
		
		return Base64.decodeBase64(data);
	}

	/**
	 * Convert PEm string format to PublicKey
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static PublicKey getPublicKey(final String key) throws Exception {
		final byte [] raw = convertFromPEM(key.getBytes());
		final X509EncodedKeySpec spec = new X509EncodedKeySpec(raw);
		final KeyFactory kf = getKeyFactory();
	    return kf.generatePublic(spec);
	}

	/**
	 * Encrypt data with public key and encode result in base64 string
	 * @param data
	 * @param key
	 * @return
	 */
	public static String encrypt(final byte[] data, final PublicKey key) {
    	final byte [] enc = encryptData(data, key);
    	return Base64.encodeBase64String(enc);		
	}
    
    /**
     * Encrypt data with public key
     * @param Buffer
     * @param key
     * @return
     */
	private static byte[] encryptData(final byte[] data, final PublicKey key) {
		
		byte[] result = null;
		
	    try {
	    	
	    	final Cipher rsa = Cipher.getInstance(RSA_MODE);
	        rsa.init(Cipher.ENCRYPT_MODE, key);
	        result = rsa.doFinal(data);
	        
	    } catch (Exception e) {
	    	result = new byte [0];
	    	LOG.error(e.getMessage());
	    	LOG.debug(e.getMessage(), e);
	    }
	    
	    return result;
	}

}
