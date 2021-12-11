package com.example.datapostgresliquibasetestcontainers.repository;

import reactor.core.publisher.Flux;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.example.datapostgresliquibasetestcontainers.domain.Book;

@Repository
public interface BookRepository extends R2dbcRepository<Book, Integer> {

  Flux<Book> findAllBy(Pageable pageable);

  Flux<Book> findAllByTitleEquals(String title);

}
