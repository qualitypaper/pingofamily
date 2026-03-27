package com.qualitypaper.fluentfusion.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.ClientTlsStrategyBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.bootstrap.BootstrapConfiguration;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyStore;
import java.util.Enumeration;

/**
 * Configures SSL for Spring Cloud Config client using a custom RestTemplate with Apache HttpClient.
 * Supports mutual TLS authentication with a keystore and truststore.
 */
@Slf4j
@Configuration
@BootstrapConfiguration
public class SSLConfigServiceBootstrapConfiguration implements DisposableBean {

  private final ConfigClientProperties configClientProperties;

  private CloseableHttpClient httpClient;

  @Value("${keystore.resource:classpath:certs/client.p12}")
  private Resource keystoreResource;

  @Value("${truststore.resource:classpath:certs/truststore.p12}")
  private Resource truststoreResource;

  @Value("${keystore.password}")
  private String keystorePassword;

  @Value("${keystore.type:JKS}")
  private String keystoreType;

  @Value("${truststore.type:JKS}")
  private String truststoreType;

  public SSLConfigServiceBootstrapConfiguration(ConfigClientProperties configClientProperties) {
    this.configClientProperties = configClientProperties;
  }

  private KeyStore loadStore(Resource resource, char[] password, String storeType, String storeName) {
    log.info("Loading {} from {}", storeName, resource.getDescription());
    log.info("Keystore from the configuration: {}", keystoreResource.getFilename());
    if (!resource.exists()) {
      log.error("Resource not found: {}", resource.getDescription());
      throw new IllegalStateException("Resource not found: " + resource.getDescription());
    }
    try (var stream = resource.getInputStream()) {
      KeyStore store = KeyStore.getInstance(storeType);
      store.load(stream, password);
      log.debug("{} loaded successfully from {} with {} entries", storeName, resource.getDescription(), store.size());
      Enumeration<String> aliases = store.aliases();
      while (aliases.hasMoreElements()) {
        String alias = aliases.nextElement();
        log.info("{} alias: {}", storeName, alias);
      }
      return store;
    } catch (Exception e) {
      log.error("Failed to load {} from {}", storeName, resource.getDescription(), e);
      throw new IllegalStateException("Failed to load " + storeName + " from " + resource.getDescription(), e);
    }
  }

  /**
   * Creates an SSLContext for mutual TLS authentication.
   *
   * @return The configured SSLContext.
   * @throws Exception If the SSLContext cannot be created.
   */
  private SSLContext createSSLContext() throws Exception {
    KeyStore keyStore = loadStore(keystoreResource, keystorePassword.toCharArray(), keystoreType, "keystore");
    KeyStore trustStore = loadStore(truststoreResource, keystorePassword.toCharArray(), truststoreType, "truststore");

    KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    kmf.init(keyStore, keystorePassword.toCharArray());

    TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    tmf.init(trustStore);

    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
    log.debug("SSLContext initialized successfully with supported protocols: {}", String.join(",", sslContext.getSupportedSSLParameters().getProtocols()));
    return sslContext;
  }

  /**
   * Creates a CloseableHttpClient with the configured SSLContext.
   *
   * @return The configured CloseableHttpClient.
   * @throws Exception If the HttpClient cannot be created.
   */
  @Bean(destroyMethod = "close")
  public CloseableHttpClient httpClient() throws Exception {
    SSLContext sslContext = createSSLContext();

    var tlsStrategy = ClientTlsStrategyBuilder.create()
            .setSslContext(sslContext)
            .setHostnameVerifier(new NoopHostnameVerifier())
            .buildClassic();

    var connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
            .setTlsSocketStrategy(tlsStrategy)
            .setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(Timeout.ofSeconds(30)).build())
            .build();

    this.httpClient = HttpClients.custom()
            .setConnectionManager(connectionManager)
            .build();

    log.info("CloseableHttpClient created with custom SSL configuration");

    return this.httpClient;
  }

  @Bean
  @Primary
  public ConfigServicePropertySourceLocator configServerPropertySourceLocator() throws Exception {
    log.debug("Configuring custom ConfigServicePropertySourceLocator");
    ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient());
    RestTemplate restTemplate = new RestTemplate(requestFactory);
    ConfigServicePropertySourceLocator locator = new ConfigServicePropertySourceLocator(configClientProperties);
    locator.setRestTemplate(restTemplate);
    log.info("Custom ConfigServicePropertySourceLocator configured successfully");
    return locator;
  }

  /**
   * Closes the HttpClient when the application shuts down.
   *
   * @throws Exception If the HttpClient cannot be closed.
   */
  @Override
  public void destroy() throws Exception {
    if (httpClient != null) {
        log.info("---------------------------------------------------------------");
        log.info("Closing HttpClient");
        log.info("---------------------------------------------------------------");
        httpClient.close();
    }
  }
}