package com.example.soapproxy.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

@RestController
public class Proxy {
    private static final Logger LOGGER = LoggerFactory.getLogger(Proxy.class);

    @PostMapping(path = "/soap-proxy", produces = APPLICATION_XML_VALUE, consumes = APPLICATION_XML_VALUE)
    public ResponseEntity<String> soapProxy(@RequestBody String body, @RequestHeader MultiValueMap<String, String> headers) {
        LOGGER.info("body: {}, headers: {}", body, headers);
        var responseBody = body;
        var responseHeaders = new HttpHeaders(headers);
        return ResponseEntity.ok().headers(responseHeaders).body(responseBody);
    }
}
