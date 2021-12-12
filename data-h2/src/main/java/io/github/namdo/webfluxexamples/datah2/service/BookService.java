package io.github.namdo.webfluxexamples.datah2.service;

import io.github.namdo.webfluxexamples.datah2.domain.Book;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.data.domain.Pageable;

public interface BookService {

  Mono<Book> save(Book book);

  Mono<Book> partialUpdate(Book book);

  Flux<Book> findAll();

  Flux<Book> findAllByPagination(Pageable pageable);

  Mono<Boolean> existsById(Integer id);

  Mono<Book> findOne(Integer id);

  Mono<Void> delete(Integer id);
}
