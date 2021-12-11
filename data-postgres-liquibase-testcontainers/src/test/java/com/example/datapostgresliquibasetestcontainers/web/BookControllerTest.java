package com.example.datapostgresliquibasetestcontainers.web;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.server.ResponseStatusException;

import com.example.datapostgresliquibasetestcontainers.domain.Book;
import com.example.datapostgresliquibasetestcontainers.service.BookService;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

  private BookController bookController;

  @Mock
  private BookService bookService;

  @BeforeEach
  public void setUp() {
    bookController = new BookController(bookService);
  }

  @AfterEach
  public void tearDown() {
    verifyNoMoreInteractions(bookService);
  }

  @Test
  void testCreateBookWithId() {
    // Setup
    final Book book = Book.builder()
        .id(BOOK_ID_1)
        .title(TITLE)
        .build();

    // Execute
    final Mono<Book> error = bookController.createBook(book);

    // Verify
    StepVerifier.create(error)
        .expectError(ResponseStatusException.class)
        .verify();
  }

  @Test
  void testCreateBookWithoutId() {
    // Setup
    final Book book = Book.builder()
        .title(TITLE)
        .build();

    final Book expected = Book.builder()
        .title(TITLE)
        .build();

    when(bookService.save(book)).thenReturn(Mono.just(expected));

    // Execute
    final Mono<Book> bookMono = bookController.createBook(book);

    // Verify
    StepVerifier.create(bookMono)
        .expectNext(expected)
        .verifyComplete();

    verify(bookService).save(book);
  }

  @Test
  void testUpdateBookWithoutId() {
    // Setup
    final Book book = Book.builder()
        .title(TITLE)
        .build();

    // Execute
    final Mono<Book> bookMono = bookController.updateBook(book);

    // Verify
    StepVerifier.create(bookMono)
        .expectError(ResponseStatusException.class)
        .verify();
  }

  @Test
  void testUpdateBookWithId() {
    // Setup
    final Book book = Book.builder()
        .id(BOOK_ID_1)
        .title(TITLE)
        .build();

    final Book expected = Book.builder()
        .title(TITLE)
        .build();

    when(bookService.save(book)).thenReturn(Mono.just(expected));

    // Execute
    final Mono<Book> bookMono = bookController.updateBook(book);

    // Verify
    StepVerifier.create(bookMono)
        .expectNext(expected)
        .verifyComplete();

    verify(bookService).save(book);
  }

  @Test
  void testGetAll() {
    // Setup
    final Book book1 = Book.builder()
        .id(BOOK_ID_1)
        .title(TITLE)
        .build();

    final Book book2 = Book.builder()
        .id(BOOK_ID_2)
        .title(TITLE)
        .build();

    when(bookService.findAll()).thenReturn(Flux.just(book1, book2));

    // Execute
    final Flux<Book> bookFlux = bookController.getAllBooks();

    // Verify
    StepVerifier.create(bookFlux)
        .expectNext(Book.builder()
            .id(BOOK_ID_1)
            .title(TITLE)
            .build())
        .expectNext(Book.builder()
            .id(BOOK_ID_2)
            .title(TITLE)
            .build())
        .verifyComplete();

    verify(bookService).findAll();
  }

  @Test
  void testGetBooksByPagination() {
    // Setup
    final Book book1 = Book.builder()
        .id(BOOK_ID_1)
        .title(TITLE)
        .build();

    final Book book2 = Book.builder()
        .id(BOOK_ID_2)
        .title(TITLE)
        .build();

    when(bookService.findAllByPagination(PageRequest.of(PAGE, SIZE))).thenReturn(Flux.just(book1, book2));

    // Execute
    final Flux<Book> bookFlux = bookController.getBooksByPagination(PAGE, SIZE);

    // Verify
    StepVerifier.create(bookFlux)
        .expectNext(Book.builder()
            .id(BOOK_ID_1)
            .title(TITLE)
            .build())
        .expectNext(Book.builder()
            .id(BOOK_ID_2)
            .title(TITLE)
            .build())
        .verifyComplete();

    verify(bookService).findAllByPagination(PageRequest.of(PAGE, SIZE));
  }

  @Test
  void getBook() {
    // Setup
    final Book book = Book.builder()
        .id(BOOK_ID_1)
        .title(TITLE)
        .build();

    when(bookService.findOne(BOOK_ID_1)).thenReturn(Mono.just(book));

    // Execute
    final Mono<Book> bookMono = bookController.getBook(BOOK_ID_1);

    // Verify
    StepVerifier.create(bookMono)
        .expectNext(Book.builder()
            .id(BOOK_ID_1)
            .title(TITLE)
            .build())
        .verifyComplete();

    verify(bookService).findOne(BOOK_ID_1);
  }

  @Test
  void testDeleteBook() {
    when(bookService.delete(BOOK_ID_1)).thenReturn(Mono.empty());

    // Execute
    final Mono<Void> bookMono = bookController.deleteBook(BOOK_ID_1);

    // Verify
    StepVerifier.create(bookMono)
        .verifyComplete();

    verify(bookService).delete(BOOK_ID_1);
  }
}
