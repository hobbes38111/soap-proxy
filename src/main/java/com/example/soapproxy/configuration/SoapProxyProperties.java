package com.example.soapproxy.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "soap-proxy")
@Configuration
public class SoapProxyProperties {
    private String soapEndpoint;
    private String authUrl;
    private String authRedirectUri;
    private String authGrantType;
    private String authClientId;
    private String authClientSecret;
    private String trustStore = "src/main/resources/emptyStore.keystore";
    private String trustStorePassword = "storePassword";
    private String protocol;
    private int connectTimeout = 5000;
    private int connectionRequestTimeout = 5000;
    private int socketTimeout = 5000;

    public String getSoapEndpoint() {
        return soapEndpoint;
    }

    public void setSoapEndpoint(String soapEndpoint) {
        this.soapEndpoint = soapEndpoint;
    }

    public String getAuthUrl() {
        return authUrl;
    }

    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }

    public String getAuthRedirectUri() {
        return authRedirectUri;
    }

    public void setAuthRedirectUri(String authRedirectUri) {
        this.authRedirectUri = authRedirectUri;
    }

    public String getAuthGrantType() {
        return authGrantType;
    }

    public void setAuthGrantType(String authGrantType) {
        this.authGrantType = authGrantType;
    }

    public String getAuthClientId() {
        return authClientId;
    }

    public void setAuthClientId(String authClientId) {
        this.authClientId = authClientId;
    }

    public String getAuthClientSecret() {
        return authClientSecret;
    }

    public void setAuthClientSecret(String authClientSecret) {
        this.authClientSecret = authClientSecret;
    }

    public String getTrustStore() {
        return trustStore;
    }

    public void setTrustStore(String trustStore) {
        this.trustStore = trustStore;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    public void setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }
}
