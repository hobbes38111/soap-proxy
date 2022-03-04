package com.example.soapproxy.rest;

import com.example.soapproxy.configuration.SoapProxyProperties;
import com.example.soapproxy.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.http.MediaType.TEXT_XML_VALUE;

@RestController
public class Proxy {
    private static final Logger LOGGER = LoggerFactory.getLogger(Proxy.class);

    private final TokenService tokenService;
    private final SoapProxyProperties properties;
    private final RestTemplate soapClient;

    @Value("${http.client.ssl.trust-store:emptyStore.keystore}")
    private String keyStore;
    @Value("${http.client.ssl.trust-store-password:storePassword}")
    private String keyStorePassword;

    @Value("${server.port}")
    private String port;

    private static final Set<String> noCopy = Set.of(HttpHeaders.CONNECTION,
                                              HttpHeaders.TRANSFER_ENCODING);


    public Proxy(TokenService tokenService, SoapProxyProperties properties,
                 @Qualifier("soapClient") RestTemplate soapClient) {
        this.tokenService = tokenService;
        this.properties = properties;
        this.soapClient = soapClient;
    }

    @GetMapping(path = {"", "/"}, produces = TEXT_PLAIN_VALUE)
    public ResponseEntity<String> echo() {

        String response = String.format("http.client.ssl.trust-store=%s\n"
                                        + "http.client.ssl.trust-store-password=%s\n"
                                        + "soap-proxy.soap-endpoint=%s\n"
                                        + "soap-proxy.auth-redirect-uri=%s\n"
                                        + "soap-proxy.auth-url=%s\n"
                                        + "soap-proxy.auth-grant-type=%s\n"
                                        + "soap-proxy.auth-client-id=%s\n"
                                        + "soap-proxy.auth-client-secret=%s\n"
                                        + "soap-proxy.protocol=%s\n"
                                        + "server.port=%s\n",
                                        keyStore,
                                        anonymize(keyStorePassword),
                                        properties.getSoapEndpoint(),
                                        properties.getAuthRedirectUri(),
                                        properties.getAuthUrl(),
                                        properties.getAuthGrantType(),
                                        properties.getAuthClientId(),
                                        anonymize(properties.getAuthClientSecret()),
                                        properties.getProtocol(),
                                        port);

        return ResponseEntity.ok(response);

    }

    @PostMapping(path = "/soap-proxy", produces = TEXT_XML_VALUE, consumes = TEXT_XML_VALUE)
    public ResponseEntity<String> soapProxy(@RequestBody String body, @RequestHeader MultiValueMap<String, String> headers) {
        LOGGER.info("body: {}, headers: {}", body, headers);

        var response = exchange(body, headers);

        if (response.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
            tokenService.refreshToken();
            response = exchange(body, headers);
        }

        HttpHeaders responseHeaders = copyHeaders(response);

        return ResponseEntity.status(response.getStatusCode())
                .headers(responseHeaders)
                      .body(response.getBody());
    }

    private ResponseEntity<String> exchange(String body, MultiValueMap<String, String> headers) {
        var requestHeaders = new LinkedMultiValueMap<String, String>();
        requestHeaders.addAll(headers);
        requestHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + tokenService.getToken());
        return soapClient.exchange(properties.getSoapEndpoint(),
                                   HttpMethod.POST,
                                   new HttpEntity<>(body, requestHeaders),
                                   String.class);
    }

    private static String anonymize(String s) {
        return s == null ? "" : s.substring(0, 2) + "*".repeat(s.length() - 4 ) + s.substring(s.length() - 2);
    }

    private static HttpHeaders copyHeaders(ResponseEntity<String> response) {
        HttpHeaders responseHeaders = new HttpHeaders();
        response.getHeaders().entrySet().stream()
                .filter(e -> !noCopy.contains(e.getKey()))
                .forEach(e -> responseHeaders.addAll(e.getKey(), e.getValue()));
        return responseHeaders;
    }

}
