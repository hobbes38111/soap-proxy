package com.example.soapproxy.configuration;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.NonNull;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

@Configuration
public class RestTemplateConfiguration {
    @Value("${http.client.ssl.trust-store:emptyStore.keystore}")
    private Resource keyStore;
    @Value("${http.client.ssl.trust-store-password:storePassword}")
    private String keyStorePassword;

    @Bean
    @Profile("!notrust")
    @Qualifier("authClient")
    @SuppressWarnings({"java:S112"})
    RestTemplate authClient() throws Exception {
        HttpComponentsClientHttpRequestFactory factory = getRequestFactory();
        return new RestTemplate(factory);
    }


    @Bean
    @Profile("!notrust")
    @Qualifier("soapClient")
    @SuppressWarnings({"java:S112"})
    RestTemplate soapClient() throws Exception {
        HttpComponentsClientHttpRequestFactory factory = getRequestFactory();
        var restTemplate = new RestTemplate(factory);
        restTemplate.setErrorHandler(noopErrorHandler());
        return restTemplate;
    }

    @Bean
    @Profile("notrust")
    @Qualifier("authClient")
    RestTemplate authClientNoSllVerification() {
        return new RestTemplate();
    }

    @Bean
    @Profile("notrust")
    @Qualifier("soapClient")
    RestTemplate soapClientNoSllVerification() {
        var template = new RestTemplate();
        template.setErrorHandler(noopErrorHandler());
        return template;
    }

    private HttpComponentsClientHttpRequestFactory getRequestFactory() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, CertificateException, IOException {
        SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(
                        keyStore.getURL(),
                        keyStorePassword.toCharArray()
                ).build();
        SSLConnectionSocketFactory socketFactory =
                new SSLConnectionSocketFactory(sslContext);
        CloseableHttpClient httpClient = HttpClients.custom()
                                                    .setSSLSocketFactory(socketFactory).build();
        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    private ResponseErrorHandler noopErrorHandler() {
        return new ResponseErrorHandler() {
            @Override
            public boolean hasError(@NonNull ClientHttpResponse response) {
                return false;
            }

            @Override
            public void handleError(@NonNull ClientHttpResponse response) {
                // not used
            }
        };
    }


}
