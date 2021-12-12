package com.example.datapostgresliquibasetestcontainers.service.impl;

import static java.util.Objects.nonNull;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.datapostgresliquibasetestcontainers.domain.Book;
import com.example.datapostgresliquibasetestcontainers.repository.BookRepository;
import com.example.datapostgresliquibasetestcontainers.service.BookService;

@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {

  private final BookRepository bookRepository;

  @Override
  public Mono<Book> save(final Book book) {
    return bookRepository.save(book);
  }

  @Override
  public Mono<Book> partialUpdate(Book book) {
    return bookRepository
        .findById(book.getId())
        .map(existingBook -> {
          if (nonNull(book.getTitle())) {
            existingBook.setTitle(book.getTitle());
          }

          if (nonNull(book.getDescription())) {
            existingBook.setDescription(book.getTitle());
          }

          return existingBook;
        })
        .flatMap(bookRepository::save);
  }

  @Override
  @Transactional(readOnly = true)
  public Flux<Book> findAll() {
    return bookRepository.findAll();
  }

  @Override
  @Transactional(readOnly = true)
  public Flux<Book> findAllByPagination(final Pageable pageable) {
    return bookRepository.findAllBy(pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public Mono<Boolean> existsById(Integer id) {
    return bookRepository.existsById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public Mono<Book> findOne(final Integer id) {
    return bookRepository.findById(id);
  }

  @Override
  public Mono<Void> delete(final Integer id) {
    return bookRepository.deleteById(id);
  }
  
}
