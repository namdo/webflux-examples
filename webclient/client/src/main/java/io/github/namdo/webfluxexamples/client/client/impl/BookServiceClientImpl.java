package io.github.namdo.webfluxexamples.client.client.impl;

import static io.github.namdo.webfluxexamples.client.utils.Constants.ID_PATH;

import io.github.namdo.webfluxexamples.client.client.BookServiceClient;
import io.github.namdo.webfluxexamples.client.dto.BookDTO;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Component
public class BookServiceClientImpl implements BookServiceClient {

  private final WebClient bookServiceWebClient;

  @Override
  public Mono<BookDTO> save(BookDTO bookDTO) {
    return bookServiceWebClient.post()
        .body(BodyInserters.fromValue(bookDTO))
        .retrieve()
        .onStatus(HttpStatus::isError, clientResponse -> Mono.error(new ResponseStatusException(clientResponse.statusCode())))
        .bodyToMono(BookDTO.class);
  }

  @Override
  public Mono<BookDTO> update(BookDTO bookDTO) {
    return bookServiceWebClient.put()
        .uri(uriBuilder -> uriBuilder
            .path(ID_PATH)
            .build(bookDTO.getId()))
        .body(BodyInserters.fromValue(bookDTO))
        .retrieve()
        .onStatus(HttpStatus::isError, clientResponse -> Mono.error(new ResponseStatusException(clientResponse.statusCode())))
        .bodyToMono(BookDTO.class);
  }

  @Override
  public Mono<BookDTO> patch(BookDTO bookDTO) {
    return bookServiceWebClient.patch()
        .uri(uriBuilder -> uriBuilder
            .path(ID_PATH)
            .build(bookDTO.getId()))
        .body(BodyInserters.fromValue(bookDTO))
        .retrieve()
        .onStatus(HttpStatus::isError, clientResponse -> Mono.error(new ResponseStatusException(clientResponse.statusCode())))
        .bodyToMono(BookDTO.class);
  }

  @Override
  public Flux<BookDTO> findAll() {
    return bookServiceWebClient.get()
        .uri("/all")
        .retrieve()
        .bodyToFlux(BookDTO.class);
  }

  @Override
  public Flux<BookDTO> findAllByPagination(int page, int size) {
    return bookServiceWebClient.get()
        .uri(uriBuilder -> uriBuilder
            .queryParam("page", page)
            .queryParam("size", size)
            .build()
        )
        .retrieve()
        .onStatus(HttpStatus::isError, clientResponse -> Mono.error(new ResponseStatusException(clientResponse.statusCode())))
        .bodyToFlux(BookDTO.class);
  }

  @Override
  public Mono<BookDTO> findOne(Integer id) {
    return bookServiceWebClient.get()
        .uri(uriBuilder -> uriBuilder
            .path(ID_PATH)
            .build(id))
        .retrieve()
        .onStatus(HttpStatus::isError, clientResponse -> Mono.error(new ResponseStatusException(clientResponse.statusCode())))
        .bodyToMono(BookDTO.class);
  }

  @Override
  public Mono<Void> delete(Integer id) {
    return bookServiceWebClient.get()
        .uri(uriBuilder -> uriBuilder
            .path(ID_PATH)
            .build(id))
        .retrieve()
        .onStatus(HttpStatus::isError, clientResponse -> Mono.error(new ResponseStatusException(clientResponse.statusCode())))
        .bodyToMono(Void.class);
  }
}
