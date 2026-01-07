/*
 * Copyright (C) 2015 - 2023 Green Screens Ltd.
 */
package io.greenscreens.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AES encryption class
 */
final class Aes {

	private static Logger LOG = LoggerFactory.getLogger(Aes.class);
	
	final private static byte[] ALPHANUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".getBytes();
	final private static Charset UTF8 = StandardCharsets.UTF_8;
	
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
	 * @throws IOException 
	 */
	public static Aes get() throws IOException {
		Aes crypt = new Aes();
		return crypt;
	}

	/**
	 * AES constructor
	 * @throws IOException 
	 */
	public Aes() throws IOException {
		super();		
		final byte[] k = getRandomString(16);
		final byte[] i = getRandomString(16);
		init(k, i);
	}

	public Aes(final byte[] data) throws IOException {
		validate(data, 32);
		final byte[] aesKey = Arrays.copyOfRange(data, 0, 16);
		final byte[] aesIV = Arrays.copyOfRange(data, 16, 32);
		init(aesKey, aesIV);
	}
	
	public Aes(final byte[] key, final byte[] iv) throws IOException {
		init(key, iv);
	}
	
	private void init(final byte[] key, final byte[] iv) throws IOException {
		validate(key, 16);
		validate(iv, 16);
		keyspec = new SecretKeySpec(key, "AES");
		ivspec = new IvParameterSpec(iv);				
	}

	private void validate(final byte[] data, final int len) throws IOException {
		if (Objects.isNull(data) || data.length != len) {
			throw new IOException("Invalid AES encryption key!");
		}				
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

		if (Objects.isNull(text) || text.length() == 0) {
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
	protected static byte[] getRandomString(final int size)  {
		
		final ByteBuffer cb = ByteBuffer.allocate(size);
		
        for (int i = 0; i < size; i++) {
            int rndIndex = randomSecureRandom.nextInt(ALPHANUM.length);
            cb.put(ALPHANUM[rndIndex]);
        }
        return cb.array();
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

		final StringBuilder sb = new StringBuilder(source);

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

		if (Objects.isNull(data)) {
			return null;
		}

		int len = data.length;
		final StringBuilder sb = new StringBuilder();

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

		if (Objects.isNull(str) || str.length() < 2) {
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
