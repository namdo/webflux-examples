package io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.service;

import io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.service.dto.BookDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.data.domain.Pageable;

public interface BookService {

  Mono<BookDTO> save(BookDTO bookDTO);

  Mono<BookDTO> partialUpdate(BookDTO bookDTO);

  Flux<BookDTO> findAll();

  Flux<BookDTO> findAllByPagination(Pageable pageable);

  Mono<Boolean> existsById(Integer id);

  Mono<BookDTO> findOne(Integer id);

  Mono<Void> delete(Integer id);
}
