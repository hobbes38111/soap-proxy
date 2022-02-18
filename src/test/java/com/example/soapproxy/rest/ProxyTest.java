package com.example.soapproxy.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ProxyTest {

    @Autowired
    private MockMvc mvc;

    @LocalServerPort
    int port;

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
}
