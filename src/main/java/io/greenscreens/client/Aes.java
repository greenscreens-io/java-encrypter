/*
 * Copyright (C) 2015 - 2018 Green Screens Ltd.
 */
package io.greenscreens.client;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;


/**
 * AES encryption class
 */
final class Aes {

	private static Log LOG = LogFactory.getLog(Aes.class);
	
	final private static Charset UTF8 = Charset.forName("UTF-8");
	
	private static Cipher cipher;
	private static SecureRandom randomSecureRandom; 

	private IvParameterSpec ivspec;
	private SecretKeySpec keyspec;

	static {
		try {
			cipher = Cipher.getInstance("AES/CTR/NoPadding");	
			randomSecureRandom = SecureRandom.getInstance("SHA1PRNG");
		} catch (GeneralSecurityException e) {
			LOG.error(e.getMessage(), e);
		}
	}
		
	/**
	 * AES instance factory
	 * @return
	 */
	public static Aes get() {
		Aes crypt = new Aes();
		return crypt;
	}

	/**
	 * AES constructor
	 */
	public Aes() {
		super();
		
		final String k = getRandomString(16);
		final String i = getRandomString(16);
		
		keyspec = new SecretKeySpec(k.getBytes(), "AES");
		ivspec = new IvParameterSpec(i.getBytes());
	}

	/**
	 * Create AES IV-KEY byte array
	 * @return
	 */
	protected byte [] getSpec() {
		return ByteBuffer.allocate(32).put(ivspec.getIV()).put(keyspec.getEncoded()).array();
	}
	
	/**
	 * Encrypt text with provided initialization vector 
	 * @param text
	 * @param iv
	 * @return
	 * @throws Exception
	 */
	private byte[] encryptData(final String text, final IvParameterSpec iv) throws Exception {

		if (text == null || text.length() == 0) {
			throw new Exception("Empty string");
		}

		byte[] encrypted = null;

		try {
			cipher.init(Cipher.ENCRYPT_MODE, keyspec, iv);
			encrypted = cipher.doFinal(padString(text).getBytes(UTF8));
		} catch (Exception e) {
			LOG.error(e.getMessage());
			throw e;
		}

		return encrypted;
	}

	/**
	 * Encrypts string to hex string
	 */
	public String encrypt(final String text) throws Exception {
		return bytesToHex(encryptData(text, ivspec));
	}

	/**
	 * Create random aes key and initialization vector
	 * @param size
	 * @return
	 */
	protected static byte[] getRandom(final int size)  {
		final byte[] iv = new byte[size];
		randomSecureRandom.nextBytes(iv);
		return iv;
	}
	
	/**
	 * Helper 
	 * @param size
	 * @return
	 */
	private static String getRandomString(int size) {
		RandomStringGenerator randomStringGenerator =
		        new RandomStringGenerator.Builder()
		                .withinRange('0', 'z')
		                .filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
		                .build();
		return randomStringGenerator.generate(size); 
	}
	
	/**
	 * Blank padding for AES algorithm
	 * 
	 * @param source
	 * @return
	 */
	public static String padString(final String source) {

		final char paddingChar = ' ';
		final int size = 16;
		final int x = source.length() % size;
		final int padLength = size - x;

		final StringBuffer sb = new StringBuffer(source);

		for (int i = 0; i < padLength; i++) {
			sb.append(paddingChar);
		}

		return sb.toString();
	}

	/**
	 * Converts raw bytes to string hex
	 * 
	 * @param data
	 * @return
	 */
	public static String bytesToHex(final byte[] data) {

		if (data == null) {
			return null;
		}

		int len = data.length;
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < len; i++) {
			if ((data[i] & 0xFF) < 16) {
				sb.append("0");
				sb.append(Integer.toHexString(data[i] & 0xFF));
			} else {
				sb.append(Integer.toHexString(data[i] & 0xFF));
			}
		}
		return sb.toString();
	}

	/**
	 * Convert string hex to raw byte's
	 * 
	 * @param str
	 * @return
	 */
	public static byte[] hexToBytes(final String str) {

		if (str == null || str.length() < 2) {
			return null;
		}

		final int len = str.length() / 2;
		byte[] buffer = new byte[len];

		for (int i = 0; i < len; i++) {
			buffer[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
		}

		return buffer;
	}
	
}
