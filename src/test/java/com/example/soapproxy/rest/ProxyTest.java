package com.example.soapproxy.rest;

import com.example.soapproxy.service.TokenService;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.badRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.unauthorized;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
                properties = {"soap-proxy.soap-endpoint=http://localhost:8081/soap"})
@ActiveProfiles("notrust")
@AutoConfigureMockMvc
@WireMockTest(httpPort = 8081)
class ProxyTest {

    @Autowired
    private MockMvc mvc;

    @LocalServerPort
    int port;

    @MockBean TokenService tokenService;

    @BeforeEach
    void setUp() {
        stubFor(WireMock.post("/soap")
                        .withHeader("test-header", equalTo("test-value"))
                        .willReturn(aResponse()
                                            .withBody("TEST")
                                            .withHeader("test-header", "test-value")));

        stubFor(WireMock.post("/soap")
                        .withHeader("Authorization", equalTo("Bearer GOOD_TOKEN"))
                        .willReturn(aResponse()
                                            .withBody("AUTHOK")
                                            .withHeader("test-header", "test-value")));

        stubFor(WireMock.post("/soap")
                        .withHeader("Authorization", equalTo("Bearer BAD_TOKEN"))
                        .willReturn(unauthorized()));

        stubFor(WireMock.post("/soap")
                        .withHeader("Authorization", equalTo("Bearer BAD_DATA"))
                        .withRequestBody(equalTo("BAD_DATA"))
                        .willReturn(badRequest()));

        stubFor(WireMock.post("/soap")
                        .withHeader("Authorization", equalTo("Bearer SERVER_ERROR"))
                        .withRequestBody(equalTo("SERVER_ERROR"))
                        .willReturn(serverError()));

        reset(tokenService);
    }

    @Test
    void testEcho() throws Exception {
        mvc.perform(get("http://localhost:{port}/", port))
           .andDo(print())
           .andExpect(content().string(containsString("server.port=0")))
           .andExpect(content().string(containsString("http.client.ssl.trust-store-password=st*********rd")))
        ;
    }

    @Test
    void testEchoRoot() throws Exception {
        mvc.perform(get("http://localhost:{port}", port))
           .andDo(print())
           .andExpect(content().string(containsString("server.port=0")))
           .andExpect(content().string(containsString("http.client.ssl.trust-store-password=st*********rd")))
        ;
    }

    @Test
    void echoMessage() throws Exception {
        mvc.perform(post("http://localhost:{port}/soap-proxy", port)
                            .accept(MediaType.APPLICATION_XML)
                            .contentType(MediaType.APPLICATION_XML)
                            .header("test-header", "test-value")
                            .content("TEST"))
           .andDo(print())
           .andExpect(status().isOk())
           .andExpect(content().string("TEST"))
           .andExpect(header().string("test-header", "test-value"));
    }

    @Test
    void testGoodToken() throws Exception {
        when(tokenService.getToken()).thenReturn("GOOD_TOKEN");

        mvc.perform(post("http://localhost:{port}/soap-proxy", port)
                            .accept(MediaType.APPLICATION_XML)
                            .contentType(MediaType.APPLICATION_XML)
                            .content("TEST"))
           .andDo(print())
           .andExpect(status().isOk())
           .andExpect(content().string("AUTHOK"));

        verify(tokenService, times(0)).refreshToken();
        verify(tokenService, times(1)).getToken();
    }

    @Test
    void testBadToken() throws Exception {
        when(tokenService.getToken()).thenReturn("BAD_TOKEN", "GOOD_TOKEN");

        mvc.perform(post("http://localhost:{port}/soap-proxy", port)
                            .accept(MediaType.APPLICATION_XML)
                            .contentType(MediaType.APPLICATION_XML)
                            .content("TEST"))
           .andDo(print())
           .andExpect(status().isOk())
           .andExpect(content().string("AUTHOK"));

        verify(tokenService, times(1)).refreshToken();
        verify(tokenService, times(2)).getToken();
    }

    @Test
    void testGeneric4xx() throws Exception {
        when(tokenService.getToken()).thenReturn("BAD_DATA");

        mvc.perform(post("http://localhost:{port}/soap-proxy", port)
                            .accept(MediaType.APPLICATION_XML)
                            .contentType(MediaType.APPLICATION_XML)
                            .content("BAD_DATA"))
           .andDo(print())
           .andExpect(status().isBadRequest());

        verify(tokenService, times(1)).getToken();
    }

    @Test
    void testGeneric5xx() throws Exception {
        when(tokenService.getToken()).thenReturn("SERVER_ERROR");

        mvc.perform(post("http://localhost:{port}/soap-proxy", port)
                            .accept(MediaType.APPLICATION_XML)
                            .contentType(MediaType.APPLICATION_XML)
                            .content("SERVER_ERROR"))
           .andDo(print())
           .andExpect(status().is5xxServerError());

        verify(tokenService, times(1)).getToken();
    }
}
