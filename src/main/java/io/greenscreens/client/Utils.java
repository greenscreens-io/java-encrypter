/*
 * Copyright (C) 2015 - 2023 Green Screens Ltd.
 */
package io.greenscreens.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

/**
 * Set of shared util methods
 */
public enum Utils {
;

	public static boolean PRINT_STACK = false;

	/**
	 * Check java version used
	 * @return
	 */
	public static int getVersion() {
	    String version = normalize(System.getProperty("java.version"));
	    if(version.startsWith("1.")) {
	        version = version.substring(2, 3);
	    } else {
	        int dot = version.indexOf(".");
	        if(dot != -1) { version = version.substring(0, dot); }
	    } return Integer.parseInt(version);
	}
	
	/**
	 * Null safe Exception error message
	 * 
	 * @param e
	 * @return
	 */
	public static String toMessage(final Throwable e) {
		return toMessage(e, Objects.isNull(e) ? "" : e.toString());
	}

	/**
	 * Convert Exception into a String message
	 * @param e
	 * @param def
	 * @return
	 */
	public static String toMessage(final Throwable e, final String def) {

		String err = normalize(def);

		if (Objects.nonNull(e)) {

			if (PRINT_STACK) {
				e.printStackTrace();
			}

			err = e.getMessage();
			if (Objects.isNull(err) && Objects.nonNull(e.getCause())) {
				err = e.getCause().getMessage();
			}

			if (Objects.isNull(err)) {
				err = def;
			}
			
			if (e instanceof NoClassDefFoundError) {
				err = String.format("%s : %s", "Class not found", err);
			}

		}

		return normalize(err);
	}
	
	
	/**
	 * Prevent null string
	 * 
	 * @param data
	 * @return
	 */
	public static String normalize(final String data) {
		return normalize(data, "");
	}

	/**
	 * If data is null, get default string
	 * 
	 * @param data
	 * @return
	 */
	public static String normalize(final String data, final String def) {

		if (Objects.isNull(data) || data.length() == 0) {
			return def;
		}

		return data.trim();

	}	

	/**
	 * Plain Java HTTP get
	 * @param urlString
	 * @return
	 * @throws IOException
	 */
    public static String sendGet(final String urlString) throws IOException {
    	
    	final URL url = new URL(urlString);
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000); // optional
        connection.setReadTimeout(5000);    // optional

        final int status = connection.getResponseCode();

        InputStream inputStream;
        if (status >= HttpURLConnection.HTTP_BAD_REQUEST) {
            inputStream = connection.getErrorStream();
        } else {
            inputStream = connection.getInputStream();
        }

        // Read the response
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        final StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line).append("\n");
        }

        reader.close();
        connection.disconnect();

        return response.toString();
    }

    // RFC 4648 Base32 alphabet
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

	/**
	 * Decode OTP key
	 * @param otpKey
	 * @return
	 */
    public static byte[] decodeOTPKey(final String otpKey) {
        // Remove padding and make uppercase
        final String base32 = otpKey.trim().replace("=", "").toUpperCase();

        int numBytes = base32.length() * 5 / 8;
        byte[] result = new byte[numBytes];

        int buffer = 0;      // bits buffer
        int bitsLeft = 0;    // how many bits in buffer
        int index = 0;       // index in result

        for (char c : base32.toCharArray()) {
            int val = ALPHABET.indexOf(c);
            if (val == -1) {
                throw new IllegalArgumentException("Invalid Base32 character: " + c);
            }

            // Append 5 bits to buffer
            buffer = (buffer << 5) | val;
            bitsLeft += 5;

            // When buffer has 8 or more bits, extract 8 bits
            if (bitsLeft >= 8) {
                result[index++] = (byte) ((buffer >> (bitsLeft - 8)) & 0xFF);
                bitsLeft -= 8;
            }
        }

        return result;
    }
    
}
