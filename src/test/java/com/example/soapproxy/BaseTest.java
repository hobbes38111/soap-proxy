package com.example.soapproxy;

import com.example.soapproxy.configuration.SoapProxyProperties;
import com.example.soapproxy.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.web.client.RestTemplateBuilder;

public abstract class BaseTest {
    protected TokenService service;

    @BeforeEach
    void setUp() {
        SoapProxyProperties properties = new SoapProxyProperties();
        properties.setAuthUrl("http://localhost:8080/auth");
        properties.setAuthRedirectUri("a_redirect_uri");
        properties.setAuthClientId("a_client");
        properties.setAuthClientSecret("a_secret");
        properties.setAuthGrantType("a_grant_type");
        service = new TokenService(properties, new RestTemplateBuilder().build());
    }
}
