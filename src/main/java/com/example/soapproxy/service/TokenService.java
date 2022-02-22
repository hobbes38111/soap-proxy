package com.example.soapproxy.service;

import com.example.soapproxy.configuration.SoapProxyProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class TokenService {

    private String token = "";

    private final SoapProxyProperties properties;
    private final RestTemplate restTemplate;

    public TokenService(SoapProxyProperties properties, RestTemplate authClient) {
        this.properties = properties;
        this.restTemplate = authClient;
    }

    public String getToken() {
        if ("".equals(token)) {
            refreshToken();
        }

        return token;
    }

    public void refreshToken() {
        HttpEntity<LinkedMultiValueMap<String, String>> entity = createHttpEntity();

        TokenResponse responseBody = executeRequest(entity);
        assert responseBody != null;
        this.token = responseBody.accessToken;
    }

    private HttpEntity<LinkedMultiValueMap<String, String>> createHttpEntity() {
        var headers = createHeaders();
        var body = createAuthRequest();

        return new HttpEntity<>(body, headers);
    }

    private HttpHeaders createHeaders() {
        var headers = new HttpHeaders();

        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        return headers;
    }

    private TokenResponse executeRequest(HttpEntity<LinkedMultiValueMap<String, String>> entity) {
        var response = restTemplate.exchange(properties.getAuthUrl(),
                                             HttpMethod.POST,
                                             entity,
                                             TokenResponse.class);
        return response.getBody();
    }

    private LinkedMultiValueMap<String, String> createAuthRequest() {
        var body = new LinkedMultiValueMap<String, String>();
        body.add("redirect_uri", properties.getAuthRedirectUri());
        body.add("grant_type", properties.getAuthGrantType());
        body.add("client_id", properties.getAuthClientId());
        body.add("client_secret", properties.getAuthClientSecret());
        return body;
    }

    private static class TokenResponse {
        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("token_type")
        private String tokenType;

        @JsonProperty("expires_in")
        private Long expiresIn;

        public TokenResponse() {
        }

        public TokenResponse(String s) {
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getTokenType() {
            return tokenType;
        }

        public void setTokenType(String tokenType) {
            this.tokenType = tokenType;
        }

        public Long getExpiresIn() {
            return expiresIn;
        }

        public void setExpiresIn(Long expiresIn) {
            this.expiresIn = expiresIn;
        }
    }
}
