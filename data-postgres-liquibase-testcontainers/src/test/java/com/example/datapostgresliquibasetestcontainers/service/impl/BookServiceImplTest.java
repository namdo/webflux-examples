package com.example.datapostgresliquibasetestcontainers.service.impl;

import static com.example.datapostgresliquibasetestcontainers.utils.TestConstants.BOOK_ID_1;
import static com.example.datapostgresliquibasetestcontainers.utils.TestConstants.BOOK_ID_2;
import static com.example.datapostgresliquibasetestcontainers.utils.TestConstants.PAGE;
import static com.example.datapostgresliquibasetestcontainers.utils.TestConstants.SIZE;
import static com.example.datapostgresliquibasetestcontainers.utils.TestConstants.TITLE;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import com.example.datapostgresliquibasetestcontainers.domain.Book;
import com.example.datapostgresliquibasetestcontainers.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

  @InjectMocks
  private BookServiceImpl bookService;

  @Mock
  private BookRepository bookRepository;

  @AfterEach
  public void tearDown() {
    verifyNoMoreInteractions(bookRepository);
  }

  @Test
  void testSave() {
    // Setup
    final Book book = Book.builder()
        .title(TITLE)
        .build();

    final Book expected = Book.builder()
        .title(TITLE)
        .id(BOOK_ID_1)
        .build();

    when(bookRepository.save(book)).thenReturn(Mono.just(expected));

    // Execute
    final Mono<Book> actualMono = bookService.save(book);

    // Verify
    StepVerifier.create(actualMono)
        .expectNext(expected)
        .verifyComplete();

    verify(bookRepository).save(book);
  }

  @Test
  void testFindAll() {
    // Setup
    final Book book1 = Book.builder()
        .id(BOOK_ID_1)
        .title(TITLE)
        .build();

    final Book book2 = Book.builder()
        .id(BOOK_ID_2)
        .title(TITLE)
        .build();

    when(bookRepository.findAll()).thenReturn(Flux.just(book1, book2));

    // Execute
    final Flux<Book> bookFlux = bookService.findAll();

    // Verify
    StepVerifier.create(bookFlux)
        .expectNext(book1)
        .expectNext(book2)
        .verifyComplete();
    verify(bookRepository).findAll();
  }

  @Test
  void testFindAllByPagination() {
    // Setup
    final Book book1 = Book.builder()
        .id(BOOK_ID_1)
        .title(TITLE)
        .build();

    final Book book2 = Book.builder()
        .id(BOOK_ID_2)
        .title(TITLE)
        .build();

    when(bookRepository.findAllBy(PageRequest.of(PAGE, SIZE))).thenReturn(Flux.just(book1, book2));

    // Execute
    final Flux<Book> bookFlux = bookService.findAllByPagination(PageRequest.of(PAGE, SIZE));

    // Verify
    StepVerifier.create(bookFlux)
        .expectNext(book1)
        .expectNext(book2)
        .verifyComplete();
    verify(bookRepository).findAllBy(PageRequest.of(PAGE, SIZE));
  }

  @Test
  void findOne() {
    // Setup
    final Book book = Book.builder()
        .id(BOOK_ID_1)
        .title(TITLE)
        .build();

    when(bookRepository.findById(BOOK_ID_1)).thenReturn(Mono.just(book));

    // Execute
    final Mono<Book> bookMono = bookService.findOne(BOOK_ID_1);

    // Verify
    StepVerifier.create(bookMono)
        .expectNext(book)
        .verifyComplete();
    verify(bookRepository).findById(BOOK_ID_1);
  }

  @Test
  void testDelete() {
    when(bookRepository.deleteById(BOOK_ID_1)).thenReturn(Mono.empty());

    // Execute
    final Mono<Void> voidMono = bookService.delete(BOOK_ID_1);

    // Verify
    StepVerifier.create(voidMono)
        .verifyComplete();

    verify(bookRepository).deleteById(BOOK_ID_1);
  }
}
