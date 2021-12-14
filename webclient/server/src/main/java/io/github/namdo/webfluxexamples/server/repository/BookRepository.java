package io.github.namdo.webfluxexamples.server.repository;

import io.github.namdo.webfluxexamples.server.domain.Book;
import reactor.core.publisher.Flux;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends R2dbcRepository<Book, Integer> {

  Flux<Book> findAllBy(Pageable pageable);

  Flux<Book> findAllByTitleEquals(String title);

}
