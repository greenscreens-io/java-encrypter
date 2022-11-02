/*
 * Copyright (C) 2015 - 2022 Green Screens Ltd.
 */
package io.greenscreens.client;

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
	    String version = System.getProperty("java.version");
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
		return toMessage(e, e == null ? "" : e.toString());
	}

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

		if (data == null || data.length() == 0) {
			return def;
		}

		return data.trim();

	}	
}
