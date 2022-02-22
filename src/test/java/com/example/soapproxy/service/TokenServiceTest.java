package com.example.soapproxy.service;

import com.example.soapproxy.BaseTest;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.assertj.core.api.Assertions.assertThat;


@WireMockTest(httpPort = 8080)
class TokenServiceTest extends BaseTest {

    @Test
    void testOk() {
        stubFor(post("/auth")
                        .willReturn(aResponse()
                                            .withHeader("Content-Type", "application/json")
                                            .withBody("{\n"
                                                      + "  \"access_token\" : \"PrqkyEUXPv496xnAD0SmaC0MkRhYgxYp8dVh5eFr\",\n"
                                                      + "  \"token_type\" : \"Bearer\",\n"
                                                      + "  \"expires_in\" : 259199\n"
                                                      + "}")));
        service.refreshToken();
        assertThat(service.getToken()).isEqualTo("PrqkyEUXPv496xnAD0SmaC0MkRhYgxYp8dVh5eFr");
    }
}
