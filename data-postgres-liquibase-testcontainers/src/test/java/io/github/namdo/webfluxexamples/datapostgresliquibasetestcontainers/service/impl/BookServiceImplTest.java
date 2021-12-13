package io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.service.impl;

import static io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.utils.TestConstants.BOOK_ID_1;
import static io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.utils.TestConstants.BOOK_ID_2;
import static io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.utils.TestConstants.DESCRIPTION;
import static io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.utils.TestConstants.NEW_DESCRIPTION;
import static io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.utils.TestConstants.NEW_TITLE;
import static io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.utils.TestConstants.PAGE;
import static io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.utils.TestConstants.SIZE;
import static io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.utils.TestConstants.TITLE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.domain.Book;
import io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.repository.BookRepository;
import io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.service.dto.BookDTO;
import io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.service.mapper.BookMapper;
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
        .title(TITLE)
        .build();

    final Book book = Book.builder()
        .title(TITLE)
        .build();

    final Book savedBook = Book.builder()
        .title(TITLE)
        .id(BOOK_ID_1)
        .build();

    final BookDTO expected = BookDTO.builder()
        .title(TITLE)
        .id(BOOK_ID_1)
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
  void testPartialUpdate() {
    // Setup
    final BookDTO bookDTO = BookDTO.builder()
        .id(BOOK_ID_1)
        .title(NEW_TITLE)
        .description(NEW_DESCRIPTION)
        .build();

    final Book retrievedBook = Book.builder()
        .id(BOOK_ID_1)
        .title(TITLE)
        .description(DESCRIPTION)
        .build();

    final Book savedBook = Book.builder()
        .id(BOOK_ID_1)
        .title(NEW_TITLE)
        .description(NEW_DESCRIPTION)
        .build();

    final BookDTO expected = BookDTO.builder()
        .id(BOOK_ID_1)
        .title(NEW_TITLE)
        .description(NEW_DESCRIPTION)
        .build();

    when(bookRepository.findById(BOOK_ID_1)).thenReturn(Mono.just(retrievedBook));
    when(bookRepository.save(savedBook)).thenReturn(Mono.just(savedBook));

    // Execute
    final Mono<BookDTO> actualMono = bookService.partialUpdate(bookDTO);

    // Verify
    StepVerifier.create(actualMono)
        .expectNext(expected)
        .verifyComplete();

    verify(bookRepository).findById(BOOK_ID_1);
    verify(bookRepository).save(savedBook);
  }

  @Test
  void testFindAll() {
    // Setup
    final BookDTO bookDTO1 = BookDTO.builder()
        .id(BOOK_ID_1)
        .title(TITLE)
        .build();

    final BookDTO bookDTO2 = BookDTO.builder()
        .id(BOOK_ID_2)
        .title(TITLE)
        .build();

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
        .id(BOOK_ID_1)
        .title(TITLE)
        .build();

    final BookDTO bookDTO2 = BookDTO.builder()
        .id(BOOK_ID_2)
        .title(TITLE)
        .build();

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
    final Flux<BookDTO> bookFlux = bookService.findAllByPagination(PageRequest.of(PAGE, SIZE));

    // Verify
    StepVerifier.create(bookFlux)
        .expectNext(bookDTO1)
        .expectNext(bookDTO2)
        .verifyComplete();
    verify(bookRepository).findAllBy(PageRequest.of(PAGE, SIZE));
  }

  @Test
  void testFindOne() {
    // Setup
    final Book book = Book.builder()
        .id(BOOK_ID_1)
        .title(TITLE)
        .build();

    final BookDTO bookDTO = BookDTO.builder()
        .id(BOOK_ID_1)
        .title(TITLE)
        .build();

    when(bookRepository.findById(BOOK_ID_1)).thenReturn(Mono.just(book));

    // Execute
    final Mono<BookDTO> bookMono = bookService.findOne(BOOK_ID_1);

    // Verify
    StepVerifier.create(bookMono)
        .expectNext(bookDTO)
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
