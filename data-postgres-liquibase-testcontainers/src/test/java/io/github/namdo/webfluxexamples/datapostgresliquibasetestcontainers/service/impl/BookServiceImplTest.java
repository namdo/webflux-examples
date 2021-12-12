package io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.domain.Book;
import io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.repository.BookRepository;
import io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.service.dto.BookDTO;
import io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.service.mapper.BookMapper;
import io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.utils.TestConstants;
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

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

  private BookServiceImpl bookService;

  @Mock
  private BookRepository bookRepository;

  @BeforeEach
  public void setUp() {
    BookMapper bookMapper = new BookMapper();
    bookService = new BookServiceImpl(bookRepository, bookMapper);
  }

  @AfterEach
  public void tearDown() {
    verifyNoMoreInteractions(bookRepository);
  }

  @Test
  void testSave() {
    // Setup
    final BookDTO bookDTO = BookDTO.builder()
        .title(TestConstants.TITLE)
        .build();

    final Book book = Book.builder()
        .title(TestConstants.TITLE)
        .build();

    final Book savedBook = Book.builder()
        .title(TestConstants.TITLE)
        .id(TestConstants.BOOK_ID_1)
        .build();

    final BookDTO expected = BookDTO.builder()
        .title(TestConstants.TITLE)
        .id(TestConstants.BOOK_ID_1)
        .build();

    when(bookRepository.save(any(Book.class))).thenReturn(Mono.just(savedBook));

    // Execute
    final Mono<BookDTO> actualMono = bookService.save(bookDTO);

    // Verify
    StepVerifier.create(actualMono)
        .expectNext(expected)
        .verifyComplete();
  }

  @Test
  void testFindAll() {
    // Setup
    final BookDTO bookDTO1 = BookDTO.builder()
        .id(TestConstants.BOOK_ID_1)
        .title(TestConstants.TITLE)
        .build();

    final BookDTO bookDTO2 = BookDTO.builder()
        .id(TestConstants.BOOK_ID_2)
        .title(TestConstants.TITLE)
        .build();

    final Book book1 = Book.builder()
        .id(TestConstants.BOOK_ID_1)
        .title(TestConstants.TITLE)
        .build();

    final Book book2 = Book.builder()
        .id(TestConstants.BOOK_ID_2)
        .title(TestConstants.TITLE)
        .build();

    when(bookRepository.findAll()).thenReturn(Flux.just(book1, book2));

    // Execute
    final Flux<BookDTO> bookFlux = bookService.findAll();

    // Verify
    StepVerifier.create(bookFlux)
        .expectNext(bookDTO1)
        .expectNext(bookDTO2)
        .verifyComplete();

    verify(bookRepository).findAll();
  }

  @Test
  void testFindAllByPagination() {
    // Setup
    final BookDTO bookDTO1 = BookDTO.builder()
        .id(TestConstants.BOOK_ID_1)
        .title(TestConstants.TITLE)
        .build();

    final BookDTO bookDTO2 = BookDTO.builder()
        .id(TestConstants.BOOK_ID_2)
        .title(TestConstants.TITLE)
        .build();

    final Book book1 = Book.builder()
        .id(TestConstants.BOOK_ID_1)
        .title(TestConstants.TITLE)
        .build();

    final Book book2 = Book.builder()
        .id(TestConstants.BOOK_ID_2)
        .title(TestConstants.TITLE)
        .build();

    when(bookRepository.findAllBy(PageRequest.of(TestConstants.PAGE, TestConstants.SIZE))).thenReturn(Flux.just(book1, book2));

    // Execute
    final Flux<BookDTO> bookFlux = bookService.findAllByPagination(PageRequest.of(TestConstants.PAGE, TestConstants.SIZE));

    // Verify
    StepVerifier.create(bookFlux)
        .expectNext(bookDTO1)
        .expectNext(bookDTO2)
        .verifyComplete();
    verify(bookRepository).findAllBy(PageRequest.of(TestConstants.PAGE, TestConstants.SIZE));
  }

  @Test
  void findOne() {
    // Setup
    final Book book = Book.builder()
        .id(TestConstants.BOOK_ID_1)
        .title(TestConstants.TITLE)
        .build();

    final BookDTO bookDTO = BookDTO.builder()
        .id(TestConstants.BOOK_ID_1)
        .title(TestConstants.TITLE)
        .build();

    when(bookRepository.findById(TestConstants.BOOK_ID_1)).thenReturn(Mono.just(book));

    // Execute
    final Mono<BookDTO> bookMono = bookService.findOne(TestConstants.BOOK_ID_1);

    // Verify
    StepVerifier.create(bookMono)
        .expectNext(bookDTO)
        .verifyComplete();
    verify(bookRepository).findById(TestConstants.BOOK_ID_1);
  }

  @Test
  void testDelete() {
    when(bookRepository.deleteById(TestConstants.BOOK_ID_1)).thenReturn(Mono.empty());

    // Execute
    final Mono<Void> voidMono = bookService.delete(TestConstants.BOOK_ID_1);

    // Verify
    StepVerifier.create(voidMono)
        .verifyComplete();

    verify(bookRepository).deleteById(TestConstants.BOOK_ID_1);
  }
}
