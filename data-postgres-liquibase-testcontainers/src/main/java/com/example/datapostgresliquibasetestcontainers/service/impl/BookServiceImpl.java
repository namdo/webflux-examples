package com.example.datapostgresliquibasetestcontainers.service.impl;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.datapostgresliquibasetestcontainers.domain.Book;
import com.example.datapostgresliquibasetestcontainers.repository.BookRepository;
import com.example.datapostgresliquibasetestcontainers.service.BookService;

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
  public Mono<Book> findOne(final Integer id) {
    return bookRepository.findById(id);
  }

  @Override
  public Mono<Void> delete(final Integer id) {
    return bookRepository.deleteById(id);
  }
}
