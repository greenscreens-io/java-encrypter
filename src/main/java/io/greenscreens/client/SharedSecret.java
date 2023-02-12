/*
 * Copyright (C) 2015 - 2023 Green Screens Ltd.
 */
package io.greenscreens.client;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Base64.Decoder;

import javax.crypto.KeyAgreement;

import org.apache.commons.codec.DecoderException;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper to work with encrypted URL for GSv6+.
 * Uses ECDH key exchange.
 */
enum SharedSecret {
;
	private static final Logger LOG = LoggerFactory.getLogger(SharedSecret.class);
	
	private static final String ALGO = "ECDH"; 
	private static final String CIPHER = "P-256"; // "prime256v1";
	
	static KeyFactory getKeyFactory() throws NoSuchAlgorithmException, NoSuchProviderException{
		return KeyFactory.getInstance(ALGO, SecurityProvider.provider());
	}
	
	static KeyPairGenerator getKeyPairGen() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException{
		final KeyPairGenerator kpgen = KeyPairGenerator.getInstance(ALGO, SecurityProvider.provider());
		kpgen.initialize(new ECGenParameterSpec(CIPHER), new SecureRandom());
		return kpgen;
	}
	
	static KeyPair newKeyPair() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
		return SharedSecret.getKeyPairGen().generateKeyPair();
	}
	
	public static byte[] doKeyExchange(final PrivateKey privateKey, final PublicKey publicKey) throws Exception {
		final KeyAgreement ka = KeyAgreement.getInstance(ALGO, SecurityProvider.provider());
		ka.init(privateKey);
		ka.doPhase(publicKey, true);
		return ka.generateSecret();
	}

	/**
	 * Generate shared secret
	 * 
	 * @param data ECDH public key from browser
	 * @param key ECDH server private key
	 * @return 32 byte (256bit) master key used for AES
	 * @throws DecoderException 
	 */
	
	public static Aes generateShared(final String data, final PrivateKey key) throws DecoderException, IOException{
		final byte[] aesData = SharedSecret.generate(data, key);
		return new Aes(aesData);
	}
	
	public static byte[] generate(final String data, final PrivateKey key) throws DecoderException {
		final Decoder base64 = Base64.getDecoder();
		byte[] bin = base64.decode(data);
		return generate(bin, key);
	}
	
	/**
	 * Decrypt data with private key and given mode
	 * 
	 * @param buffer
	 * @param key
	 * @param mode
	 * @return
	 */
	private static byte[] generate(final byte[] buffer, final PrivateKey key) {

		byte[] data = null;

		try {
			final X509EncodedKeySpec spec = new X509EncodedKeySpec(buffer);
			final KeyFactory kf = getKeyFactory();
			final PublicKey pk = kf.generatePublic(spec);
			data = SharedSecret.doKeyExchange(key, pk);
		} catch (Exception e) {
			final String msg = Utils.toMessage(e);
			LOG.error(msg);
			LOG.debug(msg, e);
			data = new byte[0];
		}

		return data;
	}

	static String flatten(final KeyPair keyPair) {
		final ECPublicKey eckey = (ECPublicKey) keyPair.getPublic();
		return Hex.toHexString(eckey.getQ().getEncoded(true));
	}

}