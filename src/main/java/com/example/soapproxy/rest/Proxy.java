package com.example.soapproxy.rest;

import com.example.soapproxy.configuration.SoapProxyProperties;
import com.example.soapproxy.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

@RestController
public class Proxy {
    private static final Logger LOGGER = LoggerFactory.getLogger(Proxy.class);

    private final TokenService tokenService;
    private final SoapProxyProperties properties;
    private final RestTemplate soapClient;

    private final Set<String> noCopy = Set.of(HttpHeaders.CONNECTION,
                                              HttpHeaders.TRANSFER_ENCODING);


    public Proxy(TokenService tokenService, SoapProxyProperties properties,
                 @Qualifier("soapClient") RestTemplate soapClient) {
        this.tokenService = tokenService;
        this.properties = properties;
        this.soapClient = soapClient;
    }

    @PostMapping(path = "/soap-proxy", produces = APPLICATION_XML_VALUE, consumes = APPLICATION_XML_VALUE)
    public ResponseEntity<String> soapProxy(@RequestBody String body, @RequestHeader MultiValueMap<String, String> headers) {
        LOGGER.info("body: {}, headers: {}", body, headers);

        ResponseEntity<String> response;
        try {
            response = exchange(body, headers);
        } catch (Exception e) {
            LOGGER.info("Received error, retrying", e);
            tokenService.refreshToken();
            response = exchange(body, headers);
        }

        HttpHeaders responseHeaders = copyHeaders(response);

        return ResponseEntity.status(response.getStatusCode())
                .headers(responseHeaders)
                      .body(response.getBody());
    }

    private HttpHeaders copyHeaders(ResponseEntity<String> response) {
        HttpHeaders responseHeaders = new HttpHeaders();
        response.getHeaders().entrySet().stream()
                .filter(e -> !noCopy.contains(e.getKey()))
                .forEach(e -> responseHeaders.addAll(e.getKey(), e.getValue()));
        return responseHeaders;
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

}
