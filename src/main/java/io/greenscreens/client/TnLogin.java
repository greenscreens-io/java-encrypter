/*
 * Copyright (C) 2015 - 2023 Green Screens Ltd.
 */
package io.greenscreens.client;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Web Terminal login data.
 * Mandatory values are UUID and HOST.
 * Other values are optional.
 */
class TnLogin {

	private String key;
	private int otp;
	
	private String uuid;	
	private String host;
	
	/**
	 * Username and password for bypass signon
	 */
	private String user;	
	private String password;

	private String program;
	private String menu;
	private String lib;

	private String displayName;
	private String printerName;
	
	/**
	 * If printer name is set,
	 * use this to enable one of available printer drivers
	 * 0 - PDF, 1- html, 2 - txt, 3 - json, 
	 * 4 - raw , 5 - img, 6 - jsx, 7 - hpt 
	 * 8 - zebram, 9 - ESCPOS, 10 - userascii, 11 - svg
	 */
	private int driver = 0;
	
	/**
	 * One of available codepages
	 * 37, 870, 424 etc...
	 */
	private String codePage;

	/**
	 * If SSL is used, and administrator enabled certificate acceess control,
	 * This should contain proper values from targeted certificate data.
	 */
	private String commonName;
	
	/**
	 * Client IP address, used for IP filtering engine
	 */
	private String ipAddress;
	
	/**
	 * Fingerprint (Application ID for mobile app)
	 */
	private long appID; 
	
	/**
	 * Mobile Application UUID
	 * If want to control access from specific mobile phone
	 */
	private String token; 
		
	/**
	 * Server timestamp synchronization
	 * Used with bypass signon feature
	 * If username and password are set, this should be set also
	 */
	private long ts = 0;

	/**
	 * Expiration timestamp
	 * After given time, encrypted URL will not be valid any more
	 */
	private long exp = 0;
	
	/**
	 * Expiration mode determine what happens link is expired
	 * 0 - strict mode - disconnect session
	 * 1 - flexible mode - shows sign-on  
	 */
	private int expMode = 0;
	
	
	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setUser(final String user) {
		if (Objects.nonNull(user) && user.startsWith("*")) throw new RuntimeException("User name cant start with a '*' character!");
		this.user = user;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(final String password) {
		this.password = password;
	}
	
	public String getProgram() {
		return program;
	}
	
	public void setProgram(final String program) {
		this.program = program;
	}
	
	public String getMenu() {
		return menu;
	}
	
	public void setMenu(final String menu) {
		this.menu = menu;
	}
	
	public String getLib() {
		return lib;
	}
	
	public void setLib(final String lib) {
		this.lib = lib;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}
	
	public String getPrinterName() {
		return printerName;
	}
	
	public void setPrinterName(final String printerName) {
		this.printerName = printerName;
	}
	
	public int getDriver() {
		return driver;
	}
	
	public void setDriver(final int driver) {
		this.driver = driver;
	}
	
	public String getCodePage() {
		return codePage;
	}
	
	public void setCodePage(final String codePage) {
		this.codePage = codePage;
	}
	
	public String getCommonName() {
		return commonName;
	}
	
	public void setCommonName(final String commonName) {
		this.commonName = commonName;
	}
	
	public String getIpAddress() {
		return ipAddress;
	}
	
	public void setIpAddress(final String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	public long getAppID() {
		return appID;
	}
	
	public void setAppID(final long appID) {
		this.appID = appID;
	}
	
	public String getToken() {
		return token;
	}
	
	public void setToken(final String token) {
		this.token = token;
	}
	
	public long getTs() {
		return ts;
	}
	
	public void setTs(final long ts) {
		this.ts = ts;
	}

	public long getExp() {
		return exp;
	}

	public void setExp(final long exp) {
		this.exp = exp;
	}

	public int getExpMode() {
		return expMode;
	}

	public void setExpMode(final int expMode) {
		this.expMode = expMode;
	}

	public String getKey() {
		return key;
	}

	public void setKey(final String key) {
		this.key = key;
	}

	public int getOtp() {
		return otp;
	}

	public void setOtp(final int otp) {
		this.otp = otp;
	}
	
	@JsonIgnore
	public String toJson() throws Exception {
		return JsonUtil.stringify(this);
	}
	
}
