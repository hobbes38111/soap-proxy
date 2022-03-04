package com.example.soapproxy.configuration;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.NonNull;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

@Configuration
public class RestTemplateConfiguration {

    private final SoapProxyProperties properties;

    public RestTemplateConfiguration(SoapProxyProperties properties) {
        this.properties = properties;
    }

    @Bean
    @Qualifier("authClient")
    @SuppressWarnings({"java:S112"})
    RestTemplate authClient() throws Exception {
        HttpComponentsClientHttpRequestFactory factory = getRequestFactory();
        return new RestTemplate(factory);
    }


    @Bean
    @Qualifier("soapClient")
    @SuppressWarnings({"java:S112"})
    RestTemplate soapClient() throws Exception {
        HttpComponentsClientHttpRequestFactory factory = getRequestFactory();
        var restTemplate = new RestTemplate(factory);
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(@NonNull ClientHttpResponse response) {
                return false;
            }

            @Override
            public void handleError(@NonNull ClientHttpResponse response) {
                // not used
            }
        });
        return restTemplate;
    }

    private HttpComponentsClientHttpRequestFactory getRequestFactory() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, CertificateException, IOException {
        var httpClient = HttpClients.custom()
                                    .setDefaultRequestConfig(getRequestConfig())
                                    .setSSLSocketFactory(getSocketFactory()).build();

        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    private SSLConnectionSocketFactory getSocketFactory() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, CertificateException, IOException {
        var sslContext = new SSLContextBuilder()
                .loadTrustMaterial(new File(properties.getTrustStore()),
                                   properties.getTrustStorePassword().toCharArray())
                .build();
        return new SSLConnectionSocketFactory(sslContext);
    }

    private RequestConfig getRequestConfig() {
        return RequestConfig.custom()
                            .setConnectTimeout(properties.getConnectTimeout())
                            .setConnectionRequestTimeout(properties.getConnectionRequestTimeout())
                            .setSocketTimeout(properties.getSocketTimeout())
                            .build();
    }


}
