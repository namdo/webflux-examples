package io.github.namdo.webfluxexamples.dataredis.service;

import io.github.namdo.webfluxexamples.dataredis.domain.Book;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.data.domain.Pageable;

public interface BookService {

  Mono<Book> save(Book book);

  Flux<Book> findAllByPagination(Pageable pageable);

  Flux<Book> findAll();

  Mono<Book> findOne(String id);

  Mono<Void> delete(String id);
}
