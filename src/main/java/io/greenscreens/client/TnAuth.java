/*
 * Copyright (C) 2015 - 2018 Green Screens Ltd.
 */
package io.greenscreens.client;

/**
 * Object to parse auth key for data encryption.
 * Key contains public key for password encryption 
 */
class TnAuth {

	private long ts;
	private int ver;
	private String key;
	private String ip;
	
	public long getTs() {
		return ts;
	}
	
	public void setTs(long ts) {
		this.ts = ts;
	}
	
	public int getVer() {
		return ver;
	}
	
	public void setVer(int ver) {
		this.ver = ver;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getIp() {
		return ip;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}

}
