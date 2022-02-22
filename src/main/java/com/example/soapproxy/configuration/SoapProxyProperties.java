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
    private String trustStore;
    private char[] trustStorePassword;
    private String protocol;

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

    public char[] getTrustStorePassword() {
        return trustStorePassword;
    }

    public void setTrustStorePassword(char[] trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
