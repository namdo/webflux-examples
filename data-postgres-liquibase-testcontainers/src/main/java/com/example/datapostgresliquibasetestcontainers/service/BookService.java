package com.example.datapostgresliquibasetestcontainers.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.data.domain.Pageable;

import com.example.datapostgresliquibasetestcontainers.domain.Book;

public interface BookService {

  Mono<Book> save(Book book);

  Mono<Book> partialUpdate(Book book);

  Flux<Book> findAll();

  Flux<Book> findAllByPagination(Pageable pageable);

  Mono<Boolean> existsById(Integer id);

  Mono<Book> findOne(Integer id);

  Mono<Void> delete(Integer id);
}
