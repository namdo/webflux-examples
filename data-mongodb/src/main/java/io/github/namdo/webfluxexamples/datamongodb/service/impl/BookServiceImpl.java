package io.github.namdo.webfluxexamples.datamongodb.service.impl;

import io.github.namdo.webfluxexamples.datamongodb.domain.Book;
import io.github.namdo.webfluxexamples.datamongodb.repository.BookRepository;
import io.github.namdo.webfluxexamples.datamongodb.service.BookService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

  private final BookRepository bookRepository;

  @Override
  public Mono<Book> save(final Book book) {
    return bookRepository.save(book);
  }

  @Override
  public Flux<Book> findAll() {
    return bookRepository.findAll();
  }

  @Override
  public Flux<Book> findAllByPagination(final Pageable pageable) {
    return bookRepository.findAllBy(pageable);
  }

  @Override
  public Mono<Book> findOne(final String id) {
    return bookRepository.findById(id);
  }

  @Override
  public Mono<Void> delete(final String id) {
    return bookRepository.deleteById(id);
  }
}
