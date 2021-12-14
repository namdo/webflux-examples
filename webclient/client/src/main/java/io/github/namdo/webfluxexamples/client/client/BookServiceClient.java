package io.github.namdo.webfluxexamples.client.client;

import io.github.namdo.webfluxexamples.client.dto.BookDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookServiceClient {

  Mono<BookDTO> save(BookDTO bookDTO);

  Mono<BookDTO> update(BookDTO bookDTO);

  Mono<BookDTO> patch(BookDTO bookDTO);

  Flux<BookDTO> findAll();

  Flux<BookDTO> findAllByPagination(int page, int size);

  Mono<BookDTO> findOne(Integer id);

  Mono<Void> delete(Integer id);
}
