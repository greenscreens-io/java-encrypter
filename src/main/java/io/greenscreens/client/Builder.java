/*
 * Copyright (C) 2015 - 2022 Green Screens Ltd.
 */
package io.greenscreens.client;

/* for build2()
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
 */
import java.net.URI;
import java.security.Key;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;

import org.apache.commons.codec.binary.Base32;
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
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;

/**
 * Green Screens Web Terminal Connection generator builder
 */
public final class Builder {

	/**
	 * To support older GS server version
	 */
	public static final String LOGIN_URL_1 = "/lite";

	/**
	 * GS Server build GT 20220725
	 */
	public static final String LOGIN_URL_2 = "/terminal";

	public static final String AUTH_URL = "/services/auth";

	public enum ExpirationMode {
		STRICT, FLEXIBLE
	}

	private String otpKey;
	private String apiKey;

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

	private int expMode;
	private long exp;
	private long ts;

	private String authUrl = AUTH_URL;
	
	/**
	 * Get builder instance with targeted browser fingerprint Use this when URL
	 * protection sharing is enabled
	 * 
	 * @param url
	 * @param fingerprint
	 * @return
	 */
	public static Builder get(final String url, final long fingerprint, final String apiKey, final String otpKey) {
		return new Builder(url, fingerprint, apiKey, otpKey);
	}

	/**
	 * Get builder instance. Do not use this if URL protection sharing is used
	 * 
	 * @param url
	 * @return
	 */
	public static Builder get(final String url, final String apiKey, final String otpKey) {
		return new Builder(url, 0, apiKey, otpKey);
	}

	/**
	 * Builder constructor
	 * 
	 * @param url
	 * @param fingerprint
	 */
	private Builder(final String url, final long fingerprint, final String apiKey, final String otpKey) {
		super();
		this.url = url;
		this.appID = fingerprint;
		this.apiKey = apiKey;
		this.otpKey = otpKey;
	}

	/**
	 * Set access UUID
	 * 
	 * @param uuid
	 * @return
	 */
	public Builder setUUID(final String uuid) {
		this.uuid = uuid;
		return this;
	}

	/**
	 * Set access virtual host name
	 * 
	 * @param host
	 * @return
	 */
	public Builder setHost(final String host) {
		this.host = host;
		return this;
	}

	/**
	 * Set remote system username
	 * 
	 * @param user
	 * @return
	 */
	public Builder setUser(final String user) {
		this.user = user;
		return this;
	}

	/**
	 * Set remote system password
	 * 
	 * @param password
	 * @return
	 */
	public Builder setPassword(final String password) {
		this.password = password;
		return this;
	}

	public Builder setProgram(final String program) {
		this.program = program;
		return this;
	}

	public Builder setMenu(final String menu) {
		this.menu = menu;
		return this;
	}

	public Builder setLib(final String lib) {
		this.lib = lib;
		return this;
	}

	public Builder setDisplayName(final String displayName) {
		this.displayName = displayName;
		return this;
	}

	public Builder setPrinterName(final String printerName) {
		this.printerName = printerName;
		return this;
	}

	public Builder setDriver(final int driver) {
		this.driver = driver;
		return this;
	}

	public Builder setCodePage(final String codePage) {
		this.codePage = codePage;
		return this;
	}

	public Builder setCommonName(final String commonName) {
		this.commonName = commonName;
		return this;
	}

	/**
	 * Set client IP address
	 * 
	 * @param ipAddress
	 * @return
	 */
	public Builder setIpAddress(final String ipAddress) {
		this.ipAddress = ipAddress;
		return this;
	}

	/**
	 * Set Mobile application access token
	 * 
	 * @param token
	 * @return
	 */
	public Builder setToken(final String token) {
		this.token = token;
		return this;
	}

	/**
	 * Determine what server will do if auto-login token is expired In felxible
	 * mode, it will show signon screen In strict mode, it will disconnect sessio
	 * 
	 * @param mode
	 * @return
	 */
	public Builder setExpirationMode(final ExpirationMode mode) {
		this.expMode = mode.ordinal();
		return this;
	}

	/**
	 * Set encrypted url expiration in seconds.
	 * 
	 * @param value
	 * @param unit
	 * @return
	 */
	public Builder setExpiration(final long value, final TimeUnit unit) {

		if (value == 0) {
			this.exp = 0;
		} else {
			this.exp = unit.toMillis(value);
		}
		return this;
	}

	/**
	 * Get url encrypted expiration fixed to server time
	 * 
	 * @return
	 */
	private long getExpiration() {
		return exp + ts;
	}

	private Builder setTimestamp(final long timestamp) {

		long diff = System.currentTimeMillis() - timestamp;

		if (diff < 0) {
			ts = System.currentTimeMillis() - diff;
		} else {
			ts = System.currentTimeMillis() + diff;
		}

		return this;
	}

	private int getOtpToken() {
		int token = 0;
		if (otpKey != null && otpKey.trim().length() > 0) {
			try {
				final TimeBasedOneTimePasswordGenerator totp = new TimeBasedOneTimePasswordGenerator();

				final byte[] data = new Base32().decode(otpKey);
				final Key key = new SecretKeySpec(data, totp.getAlgorithm());

				final Instant now = Instant.now();
				token = totp.generateOneTimePassword(key, now);

			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return token;
	}

	private TnLogin getLogin() {

		final int otpToken = getOtpToken();
		final TnLogin login = new TnLogin();
		login.setKey(apiKey);
		login.setOtp(otpToken);
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
		login.setTs(ts);
		login.setExp(getExpiration());
		// login.setTs(getExpiration());
		login.setExpMode(expMode);

		return login;
	}

	/**
	 * Returns server authorization info
	 * @return
	 * @throws Exception
	 */
	private String getServerData() throws Exception {

		final URIBuilder builder = new URIBuilder(url + authUrl);
		builder.setParameter("modern", isModern() ? "1" : "0");

		final HttpGet httpGet = new HttpGet(builder.build());
	
		final CloseableHttpResponse response = Builder.noSslHttpClient().execute(httpGet);
		final AuthResponse authResp = new AuthResponse(response);
		final String data = authResp.returnContent().asString();

		authResp.discardContent();
		return data;
	}

	/*
	 * Java 17 Http client; no need for apache http lib. public URI build2() throws
	 * Exception {
	 * 
	 * final SSLContext sslContext = new SSLContextBuilder()
	 * .loadTrustMaterial(null, new TrustStrategy() { public boolean
	 * isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
	 * return true; } }) .build();
	 * 
	 * 
	 * final HttpClient httpClient = HttpClient.newBuilder()
	 * .version(HttpClient.Version.HTTP_1_1) .connectTimeout(Duration.ofSeconds(10))
	 * .sslContext(sslContext) .build();
	 * 
	 * final HttpRequest request = HttpRequest.newBuilder() .GET()
	 * .uri(URI.create(url + authUrl)) .setHeader("User-Agent",
	 * "Green Screens Client") .build();
	 * 
	 * final HttpResponse<String> response = httpClient.send(request,
	 * HttpResponse.BodyHandlers.ofString());
	 * 
	 * final String data = response.body();
	 * 
	 * return dataToUri(data); }
	 */

	/**
	 * Generate JSON object from builder. Used for testing / debugging
	 * @return
	 * @throws Exception
	 */
	public String toJSON() throws Exception {

		final String data = getServerData();
		final TnAuth auth = JsonUtil.parse(TnAuth.class, data);

		setTimestamp(auth.getTs());
		final TnLogin login = getLogin();

		return JsonUtil.stringify(login);
	}

	/**
	 * Generate access URL
	 * 
	 * @return
	 * @throws Exception
	 */
	public URI build() throws Exception {
		final String data = getServerData();
		return dataToUri(data);
	}

	public URI dataToUri(final String data) throws Exception {

		final TnAuth auth = JsonUtil.parse(TnAuth.class, data);
		final Aes aesCrypt = Aes.get();
		final PublicKey pk = RsaUtil.getPublicKey(auth.getKey());

		setTimestamp(auth.getTs());
		final TnLogin login = getLogin();

		final String json = JsonUtil.stringify(login);
		final String aesJson = aesCrypt.encrypt(json);
		final String enc = RsaUtil.encrypt(aesCrypt.getSpec(), pk, isModern());
		final String v = Integer.toString(Long.toString(appID).hashCode());

		final String service = auth.getBuild() >= 20220725 ? LOGIN_URL_2 : LOGIN_URL_1;

		final URIBuilder builder = new URIBuilder(url + service).setParameter("d", aesJson).setParameter("k", enc).setParameter("v", v);
		
		if (auth.getVer() > 5 || isModern()) {
			builder.setParameter("t", "1");
		}
		
		return builder.build();
	}

	/**
	 * Check if modern browser encryption used or legacy 
	 * @return
	 */
	private boolean isModern() {
		return url.startsWith("https");
	}
	
	/**
	 * HTTP Client with support for SSL NOTE - for TLS 1.3 Java 12+ is required
	 * 
	 * @return
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 */
	private static CloseableHttpClient noSslHttpClient()
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {

		final SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
			public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				return true;
			}
		}).build();

		// Java 11 or newer supports TLSv1.3
		final int ver = Utils.getVersion();
		final String[] versions = ver > 11 ? new String[] { "TLSv1.2", "TLSv1.3" } :new String[] { "TLSv1.2"};

		final SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sslContext, versions, null,
				NoopHostnameVerifier.INSTANCE);

		final PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(
				RegistryBuilder.<ConnectionSocketFactory>create()
						.register("http", PlainConnectionSocketFactory.INSTANCE).register("https", factory).build());

		return HttpClients.custom().setSSLSocketFactory(factory).setConnectionManager(manager).build();
	}


}
