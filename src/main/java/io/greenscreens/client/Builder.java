/*
 * Copyright (C) 2015 - 2018 Green Screens Ltd.
 */
package io.greenscreens.client;

import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.client.fluent.AuthResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;

/**
 * Green Screens Web Terminal Connection generator builder
 */
final public class Builder {

	private String url;
	
	private String uuid;
	private String host;
	private String user;
	private String password;

	private String program;
	private String menu;
	private String lib;

	private String displayName;
	private String printerName;
	private int driver;
	
	private String codePage;

	private String commonName;
	
	private String ipAddress;
	
	private long appID; 
	
	private String token; 
	
	private String authUrl = "/services/auth";
	private String loginUrl = "/lite";
	
	/**
	 * Get builder instance with targeted browser fingerprint
	 * Use this when URL protection sharing is enabled
	 * @param url
	 * @param fingerprint
	 * @return
	 */
	public static Builder get(String url, long fingerprint) {
		return new Builder(url, fingerprint);
	}

	/**
	 * Get builder instance.
	 * Do not use this if URL protection sharing is used 
	 * @param url
	 * @return
	 */
	public static Builder get(String url) {
		return new Builder(url, 0);
	}

	/**
	 * Builder constructor
	 * @param url
	 * @param fingerprint
	 */
	private Builder(String url, long fingerprint) {
		super();
		this.url = url;
		this.appID = fingerprint;
	}

	/**
	 * Set access UUID
	 * @param uuid
	 * @return
	 */
	public Builder setUUID(String uuid) {
		this.uuid = uuid;
		return this;
	}

	/**
	 * Set access virtual host name
	 * @param host
	 * @return
	 */
	public Builder setHost(String host) {
		this.host = host;
		return this;
	}

	/**
	 * Set remote system username
	 * @param user
	 * @return
	 */
	public Builder setUser(String user) {
		this.user = user;
		return this;
	}

	/**
	 * Set remote system password
	 * @param password
	 * @return
	 */
	public Builder setPassword(String password) {
		this.password = password;
		return this;
	}

	public Builder setProgram(String program) {
		this.program = program;
		return this;
	}

	public Builder setMenu(String menu) {
		this.menu = menu;
		return this;
	}

	public Builder setLib(String lib) {
		this.lib = lib;
		return this;
	}

	public Builder setDisplayName(String displayName) {
		this.displayName = displayName;
		return this;
	}

	public Builder setPrinterName(String printerName) {
		this.printerName = printerName;
		return this;
	}

	public Builder setDriver(int driver) {
		this.driver = driver;
		return this;
	}

	public Builder setCodePage(String codePage) {
		this.codePage = codePage;
		return this;
	}

	public Builder setCommonName(String commonName) {
		this.commonName = commonName;
		return this;
	}

	/**
	 * Set client IP address
	 * @param ipAddress
	 * @return
	 */
	public Builder setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
		return this;
	}

	/**
	 * Set Mobile application access token
	 * @param token
	 * @return
	 */
	public Builder setToken(String token) {
		this.token = token;
		return this;
	}

	/**
	 * Set custom service authorization url
	 * Default is /services/auth
	 * @param authUrl
	 * @return
	 */
	public Builder setAuthUrl(String authUrl) {
		this.authUrl = authUrl;
		return this;
	}

	/**
	 * Set custom web terminal url
	 * Default is /lite
	 * @param loginUrl
	 * @return
	 */
	public Builder setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
		return this;
	}

	private TnLogin getLogin(long timestamp) {
		
		final TnLogin login = new TnLogin();
		
		login.setAppID(appID);
		login.setCodePage(codePage);
		login.setCommonName(commonName);
		login.setDisplayName(displayName);
		login.setDriver(driver);
		login.setHost(host);
		login.setIpAddress(ipAddress);
		login.setLib(lib);
		login.setMenu(menu);
		login.setPassword(password);
		login.setPrinterName(printerName);
		login.setProgram(program);		
		login.setToken(token);
		login.setUser(user);
		login.setUuid(uuid);
		
		long diff = System.currentTimeMillis() - timestamp;
		
		if (diff < 0) {
			diff = System.currentTimeMillis() - diff;
		} else {
			diff = System.currentTimeMillis() + diff;
		}
		login.setTs(diff);
		
		return login;
	}
	
	/**
	 * Generate access URL
	 * @return
	 * @throws Exception
	 */
	public URI build() throws Exception {
		
		final HttpGet httpGet = new HttpGet(url + authUrl);
		
		final CloseableHttpResponse response = noSslHttpClient().execute(httpGet);
		final AuthResponse authResp = new AuthResponse(response);
		final String data = authResp.returnContent().asString();
		
        authResp.discardContent();
		
        final TnAuth auth = JsonUtil.parse(TnAuth.class, data);
		final Aes aesCrypt = Aes.get();
		final PublicKey pk  = RsaUtil.getPublicKey(auth.getKey());		
		final TnLogin login = getLogin(auth.getTs());
		
		final String json = JsonUtil.stringify(login);
		final String aesJson = aesCrypt.encrypt(json);
		final String enc = RsaUtil.encrypt(aesCrypt.getSpec(), pk);
		
		return new URIBuilder(url +  loginUrl).setParameter("d", aesJson).setParameter("k", enc).setParameter("v", "123456789").build();

	}
	
	/**
	 * HTTP Client with support for SSL 
	 * @return
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 */
	 private static CloseableHttpClient noSslHttpClient() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {  
	 		 
		 final SSLContext sslContext = new SSLContextBuilder()  
	                .loadTrustMaterial(null, new TrustStrategy() {						
						public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
							return true;
						}
					}) .build();  
	      
	      final PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(  
                  RegistryBuilder.<ConnectionSocketFactory>create()  
                  .register("http", PlainConnectionSocketFactory.INSTANCE)  
                  .register("https", new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE))  
                  .build()  
                  );
          
	      return HttpClientBuilder.create()  
	                .setSSLContext(sslContext)  
	                .setConnectionManager(manager)  
	                .build();  
	 }  
	
}
