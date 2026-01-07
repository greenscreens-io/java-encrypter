/*
 * Copyright (C) 2015 - 2023 Green Screens Ltd.
 */
package io.greenscreens.client;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource.PSpecified;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * RSA utility to work with public RSA key
 */
enum RsaUtil {
	;

	private static Logger LOG = LoggerFactory.getLogger(RsaUtil.class);
	
	private static final String RSA_MODE = "RSA/ECB/PKCS1Padding";
	private static final String WEB_MODE = "RSA/NONE/OAEPWithSHA256AndMGF1Padding";
	private static final String WEB_MODE_JCA = "RSA/NONE/OAEPWithSHA-256AndMGF1Padding";
	private static final OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSpecified.DEFAULT);

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
		final String [] lines = data.split("\r?\n");
		
		String flat = null;
		if (lines.length>1) {
			lines[0] = "";
			lines[lines.length-1] = "";
			flat = String.join("", lines);
		} else {
			flat = lines[0];
		}

		return Base64.getDecoder().decode(flat);
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
	 * @throws NoSuchProviderException 
	 * @throws InvalidAlgorithmParameterException 
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 */
	public static String encrypt(final byte[] data, final PublicKey key, final boolean modern) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException, IllegalBlockSizeException, BadPaddingException {
    	final byte [] enc = encryptData(data, key, modern);
    	return Base64.getUrlEncoder().encodeToString(enc);		
	}
    
    /**
     * Encrypt data with public key
     * @param Buffer
     * @param key
     * @return
     * @throws NoSuchProviderException 
     * @throws InvalidAlgorithmParameterException 
     * @throws NoSuchPaddingException 
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeyException 
     * @throws BadPaddingException 
     * @throws IllegalBlockSizeException 
     */
	private static byte[] encryptData(final byte[] data, final PublicKey key, final boolean modern) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException, IllegalBlockSizeException, BadPaddingException {
		final Cipher cipher = getCipher(modern, key, Cipher.ENCRYPT_MODE);
		return cipher.doFinal(data);
	}
	
	/**
	 * Get proper cipher engine
	 * @param modern
	 * @param key
	 * @param mode
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws NoSuchProviderException
	 */
	private static Cipher getCipher(final boolean modern, final Key key, final int mode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchProviderException  {

		Cipher cipher = null;
		SecurityProvider.get();
		if (modern) {
			try {
				cipher = Cipher.getInstance(WEB_MODE, SecurityProvider.provider());
			} catch (Exception e) {
				final String msg = Utils.toMessage(e);
				LOG.error(msg);
				LOG.debug(msg, e);
				cipher = Cipher.getInstance(WEB_MODE_JCA, SecurityProvider.provider());
			}
			cipher.init(mode, key, oaepParams);
		} else {
			cipher = Cipher.getInstance(RSA_MODE);
			cipher.init(mode, key);
		}

		return cipher;
	}

}
