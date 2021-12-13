package io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.integration;

import static io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.utils.Constants.BOOKS_PATH;
import static io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.utils.TestConstants.BOOK_ID_123;
import static io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.utils.TestConstants.DESCRIPTION;
import static io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.utils.TestConstants.NEW_DESCRIPTION;
import static io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.utils.TestConstants.NEW_TITLE;
import static io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.utils.TestConstants.TITLE;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.DataPostgresLiquibaseTestcontainersApplication;
import io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.domain.Book;
import io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.repository.BookRepository;
import io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.service.dto.BookDTO;
import io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.service.mapper.BookMapper;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(classes = DataPostgresLiquibaseTestcontainersApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureWebTestClient(timeout = "360000")
class BookIT {

  @Container
  public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres").withTag("14.1"));

  @DynamicPropertySource
  static void postgresProperties(final DynamicPropertyRegistry registry) {
    registry.add("spring.r2dbc.url", () -> postgreSQLContainer.getJdbcUrl().replace("jdbc", "r2dbc"));
    registry.add("spring.r2dbc.username", postgreSQLContainer::getUsername);
    registry.add("spring.r2dbc.password", postgreSQLContainer::getPassword);

    registry.add("spring.liquibase.url", () -> postgreSQLContainer.getJdbcUrl());
    registry.add("spring.liquibase.user", postgreSQLContainer::getUsername);
    registry.add("spring.liquibase.password", postgreSQLContainer::getPassword);
  }

  private static final String BOOK_API_URL_ID = BOOKS_PATH + "/{id}";

  private static final Random random = new Random();

  private static final AtomicInteger count = new AtomicInteger(random.nextInt());

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private BookMapper bookMapper;

  @Autowired
  private WebTestClient webTestClient;

  private Book book;

  public static Book createBook() {
    return Book.builder()
        .title(TITLE)
        .description(DESCRIPTION)
        .build();
  }

  @BeforeEach
  public void setUp() {
    bookRepository.deleteAll().block();
    book = createBook();
  }

  /**
   * Tests health check
   **/
  @Test
  void testHealthCheck() {
    webTestClient.get()
        .uri("/actuator/health")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(Void.class);
  }

  @Test
  void testCreateBook() throws IOException {
    // Setup
    BookDTO bookDTO = bookMapper.toDto(book);

    // Execute
    final WebTestClient.ResponseSpec actualResponseSpec = webTestClient.post()
        .uri(BOOKS_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .accept()
        .body(BodyInserters.fromValue(bookDTO))
        .exchange();

    // Verify
    final BookDTO actualBook = actualResponseSpec.expectStatus()
        .isCreated()
        .returnResult(BookDTO.class)
        .getResponseBody()
        .blockFirst();

    assertThat(actualBook).isNotNull();
    assertThat(actualBook.getId()).isNotNull();
    assertThat(actualBook.getTitle()).isEqualTo(bookDTO.getTitle());
    assertThat(actualBook.getDescription()).isEqualTo(bookDTO.getDescription());
  }

  @Test
  void testCreateBookWithExistingId() throws IOException {
    // Setup
    book.setId(1);
    BookDTO bookDTO = bookMapper.toDto(book);

    // Execute
    final WebTestClient.ResponseSpec actualResponseSpec = webTestClient.post()
        .uri(BOOKS_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .accept()
        .body(BodyInserters.fromValue(bookDTO))
        .exchange();

    // Verify
    actualResponseSpec.expectStatus()
        .isBadRequest();
  }

  @Test
  void testGetAllBooks() {
    // Setup
    Book newBook = bookRepository.save(book).block();
    BookDTO expected = bookMapper.toDto(newBook);

    // Execute
    final WebTestClient.ResponseSpec actualResponseSpec = webTestClient.get()
        .uri(BOOKS_PATH + "/all")
        .exchange();

    // Verify
    final Flux<BookDTO> bookFlux = actualResponseSpec.expectStatus()
        .isOk()
        .returnResult(BookDTO.class)
        .getResponseBody();

    StepVerifier.create(bookFlux)
        .expectNext(expected)
        .verifyComplete();
  }

  @Test
  void testGetBooksByPagination() {
    Book book1 = Book.builder()
        .title("Title1")
        .build();

    Book book2 = Book.builder()
        .title("Title2")
        .build();

    book1 = webTestClient.post()
        .uri(BOOKS_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .accept()
        .bodyValue(book1)
        .exchange()
        .returnResult(Book.class)
        .getResponseBody()
        .blockFirst();

    book2 = webTestClient.post()
        .uri(BOOKS_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .accept()
        .bodyValue(book2)
        .exchange()
        .returnResult(Book.class)
        .getResponseBody()
        .blockFirst();

    BookDTO expected = bookMapper.toDto(book2);

    // Execute
    final WebTestClient.ResponseSpec actualResponseSpec = webTestClient.get()
        .uri(uri -> uri.path(BOOKS_PATH).queryParam("page", 1)
            .queryParam("size", 1)
            .build())
        .exchange();

    final Flux<BookDTO> bookFlux = actualResponseSpec.expectStatus()
        .isOk()
        .returnResult(BookDTO.class)
        .getResponseBody();

    // Verify
    assertThat(book1).isNotNull();
    assertThat(book2).isNotNull();

    StepVerifier.create(bookFlux)
        .expectNext(expected)
        .verifyComplete();
  }

  @Test
  void testGetBook() {
    // Setup
    Book newBook = bookRepository.save(book).block();
    BookDTO expected = bookMapper.toDto(newBook);

    // Execute
    final WebTestClient.ResponseSpec actualResponseSpec = webTestClient.get()
        .uri(BOOKS_PATH + "/{id}", newBook.getId())
        .exchange();

    // Verify
    final Flux<BookDTO> bookFlux = actualResponseSpec.expectStatus()
        .isOk()
        .returnResult(BookDTO.class)
        .getResponseBody();

    StepVerifier.create(bookFlux)
        .expectNext(expected)
        .verifyComplete();
  }

  @Test
  void testGetNonExistingBook() throws IOException {
    // Execute
    final WebTestClient.ResponseSpec actualResponseSpec = webTestClient.get()
        .uri(BOOKS_PATH + "/{id}", Integer.MAX_VALUE)
        .exchange();

    // Verify
    actualResponseSpec.expectStatus()
        .isNotFound();
  }

  @Test
  void testPutNewBook() {
    // Setup
    Book newBook = bookRepository.save(book).block();
    newBook.setTitle(NEW_TITLE);

    BookDTO expected = bookMapper.toDto(newBook);

    // Execute
    final WebTestClient.ResponseSpec actualResponseSpec = webTestClient.put()
        .uri(BOOK_API_URL_ID, newBook.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .accept()
        .bodyValue(newBook)
        .exchange();

    // Verify
    final Flux<BookDTO> bookFlux = actualResponseSpec.expectStatus()
        .isOk()
        .returnResult(BookDTO.class)
        .getResponseBody();

    StepVerifier.create(bookFlux)
        .expectNext(expected)
        .verifyComplete();
  }

  @Test
  void testPutNonExistingBook() {
    // Setup
    book.setId(count.incrementAndGet());
    BookDTO bookDTO = bookMapper.toDto(book);

    // Execute
    final WebTestClient.ResponseSpec actualResponseSpec = webTestClient.put()
        .uri(BOOK_API_URL_ID, book.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .accept()
        .bodyValue(bookDTO)
        .exchange();

    // Verify
    actualResponseSpec.expectStatus()
        .isBadRequest();
  }

  @Test
  void testPutWithIdMismatchBook() throws IOException {
    // Setup
    book.setId(count.incrementAndGet());
    BookDTO bookDTO = bookMapper.toDto(book);

    // Execute
    final WebTestClient.ResponseSpec actualResponseSpec = webTestClient.put()
        .uri(BOOK_API_URL_ID, count.incrementAndGet())
        .contentType(MediaType.APPLICATION_JSON)
        .accept()
        .body(BodyInserters.fromValue(bookDTO))
        .exchange();

    // Verify
    actualResponseSpec.expectStatus()
        .isBadRequest();
  }

  @Test
  void testPutWithMissingIdBook() throws IOException {
    // Setup
    BookDTO bookDTO = bookMapper.toDto(book);

    // Execute
    final WebTestClient.ResponseSpec actualResponseSpec = webTestClient.put()
        .uri(BOOK_API_URL_ID, BOOK_ID_123)
        .contentType(MediaType.APPLICATION_JSON)
        .accept()
        .body(BodyInserters.fromValue(bookDTO))
        .exchange();

    // Verify
    actualResponseSpec.expectStatus()
        .isBadRequest();
  }

  @Test
  void testPartialUpdateBookWithPatch() throws IOException {
    // Setup
    bookRepository.save(book).block();

    Book partialUpdatedBook = new Book();
    partialUpdatedBook.setId(book.getId());
    partialUpdatedBook.setTitle(NEW_TITLE);

    BookDTO expected = BookDTO.builder()
        .id(book.getId())
        .title(NEW_TITLE)
        .description(DESCRIPTION)
        .build();

    // Execute
    final WebTestClient.ResponseSpec actualResponseSpec = webTestClient.patch()
        .uri(BOOK_API_URL_ID, partialUpdatedBook.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(partialUpdatedBook))
        .exchange();

    // Verify
    Flux<BookDTO> bookFlux = actualResponseSpec.expectStatus()
        .isOk()
        .returnResult(BookDTO.class)
        .getResponseBody();

    StepVerifier.create(bookFlux)
        .expectNext(expected)
        .verifyComplete();
  }

  @Test
  void testFullUpdateUserEntityWithPatch() throws IOException {
    // Setup
    bookRepository.save(book).block();

    Book partialUpdatedBook = new Book();
    partialUpdatedBook.setId(book.getId());
    partialUpdatedBook.setTitle(NEW_TITLE);
    partialUpdatedBook.setDescription(NEW_DESCRIPTION);

    BookDTO expected = BookDTO.builder()
        .id(book.getId())
        .title(NEW_TITLE)
        .description(NEW_DESCRIPTION)
        .build();

    // Execute
    final WebTestClient.ResponseSpec actualResponseSpec = webTestClient.patch()
        .uri(BOOK_API_URL_ID, partialUpdatedBook.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(partialUpdatedBook))
        .exchange();

    // Verify
    Flux<BookDTO> bookFlux = actualResponseSpec.expectStatus()
        .isOk()
        .returnResult(BookDTO.class)
        .getResponseBody();

    StepVerifier.create(bookFlux)
        .expectNext(expected)
        .verifyComplete();
  }

  @Test
  void testPatchNonExistingBook() {
    // Setup
    book.setId(count.incrementAndGet());
    BookDTO bookDTO = bookMapper.toDto(book);

    // Execute
    final WebTestClient.ResponseSpec actualResponseSpec = webTestClient.patch()
        .uri(BOOK_API_URL_ID, book.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .accept()
        .bodyValue(bookDTO)
        .exchange();

    // Verify
    actualResponseSpec.expectStatus()
        .isBadRequest();
  }

  @Test
  void testPatchWithIdMismatchBook() throws IOException {
    // Setup
    book.setId(count.incrementAndGet());
    BookDTO bookDTO = bookMapper.toDto(book);

    // Execute
    final WebTestClient.ResponseSpec actualResponseSpec = webTestClient.patch()
        .uri(BOOK_API_URL_ID, count.incrementAndGet())
        .contentType(MediaType.APPLICATION_JSON)
        .accept()
        .body(BodyInserters.fromValue(bookDTO))
        .exchange();

    // Verify
    actualResponseSpec.expectStatus()
        .isBadRequest();
  }

  @Test
  void testPatchWithMissingIdBook() throws IOException {
    // Setup
    BookDTO bookDTO = bookMapper.toDto(book);

    // Execute
    final WebTestClient.ResponseSpec actualResponseSpec = webTestClient.patch()
        .uri(BOOK_API_URL_ID, BOOK_ID_123)
        .contentType(MediaType.APPLICATION_JSON)
        .accept()
        .body(BodyInserters.fromValue(bookDTO))
        .exchange();

    // Verify
    actualResponseSpec.expectStatus()
        .isBadRequest();
  }

  @Test
  void testDeleteBook() {
    // Setup
    Book newBook = bookRepository.save(book).block();

    // Execute
    final WebTestClient.ResponseSpec actualResponseSpec = webTestClient.delete()
        .uri(BOOKS_PATH + "/{id}", newBook.getId())
        .exchange();

    // Verify
    actualResponseSpec.expectStatus()
        .isNoContent();

    webTestClient.get()
        .uri(BOOKS_PATH + "/{id}", newBook.getId())
        .exchange().expectStatus()
        .isNotFound();
  }

}
