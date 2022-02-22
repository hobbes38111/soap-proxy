# Soap proxy

This service listens to POST requests on `/soap-proxy`, adds a `Bearer token` as `Authorization` header, forwards them to the specified soap endpoint, 
and returns the response from the soap endpoint.

In case a 401 is returned, the service retrieves a fresh token and retries.

## Building

run .\mvnw clean package -DskipTests from project root, then pick up a jar from target directory.

## Configuration

As spring supports many ways to configure the application, please refer to 
[spring official documentation.](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config.command-line-args)

All methods can be combined as long as the priority is respected. Consult the above document.

The simplest way to provide configuration is to provide properties as command line arguments, eg:

    java -jar soap-proxy.jar --soap-proxy.soap-endpoint=http://some-soap-endpoint ...

* http.client.ssl.trust-store - trust store location
* http.client.ssl.trust-store-password - trust store password
* soap-proxy.soap-endpoint - whole url of the soap endpoint to forward to
* soap-proxy.auth-redirect-uri - uri to send to authentication service as redirect
* soap-proxy.auth-url - authentication url
* soap-proxy.auth-grant-type - authentication grant type
* soap-proxy.auth-client-id - authentication client id
* soap-proxy.auth-client-secret - authentication client secret
* soap-proxy.trust-store - trust store location
* soap-proxy.trust-store-password - trust store password in UTF-8
* soap-proxy.protocol - SSL protocol to use
* server.port - port on which to listen
