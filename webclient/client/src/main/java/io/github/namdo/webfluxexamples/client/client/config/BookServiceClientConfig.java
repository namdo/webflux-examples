package io.github.namdo.webfluxexamples.client.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class BookServiceClientConfig extends AbstractWebClientConfig {

  @Value("${bookservice.conectTimeout}")
  private Integer connectTimeout;

  @Value("${bookservice.readTimeout}")
  private Integer readTimeout;

  @Value("${bookservice.writeTimeout}")
  private Integer writeTimeout;

  @Value("${bookservice.log}")
  private boolean logEnabled;

  @Value("${bookservice.baseUrl}")
  private String baseUrl;

  @Bean
  public WebClient bookServiceWebClient() {
    return createWebClient(baseUrl, connectTimeout, readTimeout, writeTimeout, logEnabled);
  }
}
