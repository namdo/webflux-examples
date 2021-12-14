package io.github.namdo.webfluxexamples.client.client.config;

import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Log4j2
public abstract class AbstractWebClientConfig {

  @Autowired
  private WebClient.Builder builder;

  public WebClient createWebClient(final String baseUrl, final Integer connectTimeout, final Integer readTimeout, final Integer writeTimeout, final boolean logRequest) {
    final HttpClient httpClient = HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
        .doOnConnected(connection -> connection
            .addHandlerLast(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS))
            .addHandlerLast(new WriteTimeoutHandler(writeTimeout, TimeUnit.MILLISECONDS)));

    builder.baseUrl(baseUrl)
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .exchangeStrategies(ExchangeStrategies
            .builder().codecs(codecConfigurer -> {
              ClientCodecConfigurer.ClientDefaultCodecs clientDefaultCodecs = codecConfigurer.defaultCodecs();
              clientDefaultCodecs.maxInMemorySize(1024 * 1024 * 10);
            })
            .build())
        .clientConnector(new ReactorClientHttpConnector(httpClient));

    if (logRequest) {
      builder = builder.filter(writeLogRequest());
    }

    return builder.build();
  }

  public static ExchangeFilterFunction writeLogRequest() {
    return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
      log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
      return Mono.just(clientRequest);
    });
  }

}
