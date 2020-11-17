package io.github.namdo.webfluxexamples.datamongodb.repository;

import io.github.namdo.webfluxexamples.datamongodb.domain.Book;
import reactor.core.publisher.Flux;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends ReactiveMongoRepository<Book, String> {

  Flux<Book> findAllBy(Pageable pageable);

}
