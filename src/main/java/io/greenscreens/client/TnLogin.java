/*
 * Copyright (C) 2015 - 2018 Green Screens Ltd.
 */
package io.greenscreens.client;

/**
 * Web Terminal login data.
 * Mandatory values are UUID and HOST.
 * Other values are optional.
 */
class TnLogin {

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
	 * 4 - raw , 5 - etl, 6 - jsx, 7 - hpt 
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
		
	private long ts;
	
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
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getProgram() {
		return program;
	}
	
	public void setProgram(String program) {
		this.program = program;
	}
	
	public String getMenu() {
		return menu;
	}
	
	public void setMenu(String menu) {
		this.menu = menu;
	}
	
	public String getLib() {
		return lib;
	}
	
	public void setLib(String lib) {
		this.lib = lib;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public String getPrinterName() {
		return printerName;
	}
	
	public void setPrinterName(String printerName) {
		this.printerName = printerName;
	}
	
	public int getDriver() {
		return driver;
	}
	
	public void setDriver(int driver) {
		this.driver = driver;
	}
	
	public String getCodePage() {
		return codePage;
	}
	
	public void setCodePage(String codePage) {
		this.codePage = codePage;
	}
	
	public String getCommonName() {
		return commonName;
	}
	
	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}
	
	public String getIpAddress() {
		return ipAddress;
	}
	
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	public long getAppID() {
		return appID;
	}
	
	public void setAppID(long appID) {
		this.appID = appID;
	}
	
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public long getTs() {
		return ts;
	}
	
	public void setTs(long ts) {
		this.ts = ts;
	}
	
}
