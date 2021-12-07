package io.github.namdo.webfluxexamples.dataredis.repository;

import io.github.namdo.webfluxexamples.dataredis.domain.Book;
import reactor.core.publisher.Flux;

import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends ReactiveRedisOperations<Book, String> {

  Flux<Book> findAllBy(Pageable pageable);

  Flux<Book> findAllByTitleEquals(String title);

}
