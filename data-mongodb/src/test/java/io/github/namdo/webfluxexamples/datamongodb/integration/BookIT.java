package io.github.namdo.webfluxexamples.datamongodb.integration;

import static io.github.namdo.webfluxexamples.datamongodb.utils.DataMongodbConstants.BOOKS_PATH;
import static io.github.namdo.webfluxexamples.datamongodb.utils.TestConstants.BOOK_ID_123;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import io.github.namdo.webfluxexamples.datamongodb.DataMongodbApplication;
import io.github.namdo.webfluxexamples.datamongodb.domain.Book;
import io.github.namdo.webfluxexamples.datamongodb.repository.BookRepository;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;
import org.testcontainers.utility.DockerImageName;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(classes = DataMongodbApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class BookIT {

  private static final String BOOK_API_URL_ID = BOOKS_PATH + "/{id}";

  private static ObjectMapper OBJECT_MAPPER;

  @Container
  public static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo"));

  @DynamicPropertySource
  static void mongoDbProperties(final DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
  }

  @Autowired
  private WebTestClient webTestClient;

  @Autowired
  private BookRepository bookRepository;

  @BeforeAll
  public static void beforeAll() {
    OBJECT_MAPPER = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @BeforeEach
  public void setUp() {
    bookRepository.deleteAll().block();
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
  void testCreateBookWithoutId() throws IOException {
    // Setup
    final String body = readJsonAsStringFromFile("title.json", getClass());
    final Book expectedBook = OBJECT_MAPPER.readValue(body, Book.class);

    // Execute
    final WebTestClient.ResponseSpec actualResponseSpec = webTestClient.post()
        .uri(BOOKS_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .accept()
        .bodyValue(body)
        .exchange();

    // Verify
    final Book actualBook = actualResponseSpec.expectStatus()
        .isCreated()
        .returnResult(Book.class)
        .getResponseBody()
        .blockFirst();

    assertThat(actualBook).isNotNull();
    assertThat(actualBook.getId()).isNotNull();
    assertThat(actualBook.getTitle()).isEqualTo(expectedBook.getTitle());
  }

  @Test
  void testCreateBookWithId() throws IOException {
    // Setup
    final String body = readJsonAsStringFromFile("title_id.json", getClass());

    // Execute
    final WebTestClient.ResponseSpec actualResponseSpec = webTestClient.post()
        .uri(BOOKS_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .accept()
        .bodyValue(body)
        .exchange();

    // Verify
    actualResponseSpec.expectStatus()
        .isBadRequest();
  }

  @Test
  void testUpdateBookWithoutId() throws IOException {
    // Setup
    final String body = readJsonAsStringFromFile("title.json", getClass());

    // Execute
    final WebTestClient.ResponseSpec actualResponseSpec = webTestClient.put()
        .uri(BOOK_API_URL_ID, BOOK_ID_123)
        .contentType(MediaType.APPLICATION_JSON)
        .accept()
        .bodyValue(body)
        .exchange();

    // Verify
    actualResponseSpec.expectStatus()
        .isBadRequest();
  }

  @Test
  void testUpdateBookWithId() throws IOException {
    // Setup
    final String body = readJsonAsStringFromFile("title.json", getClass());
    final Book newBook = webTestClient.post()
        .uri(BOOKS_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .accept()
        .bodyValue(body)
        .exchange()
        .expectStatus()
        .isCreated()
        .returnResult(Book.class)
        .getResponseBody()
        .blockFirst();

    newBook.setTitle("New Title");

    // Execute
    final WebTestClient.ResponseSpec actualResponseSpec = webTestClient.put()
        .uri(BOOK_API_URL_ID, newBook.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .accept()
        .bodyValue(newBook)
        .exchange();

    // Verify
    final Flux<Book> bookFlux = actualResponseSpec.expectStatus()
        .isOk()
        .returnResult(Book.class)
        .getResponseBody();

    StepVerifier.create(bookFlux)
        .expectNext(newBook)
        .verifyComplete();
  }

  @Test
  void testGetAllBooks() throws IOException {
    // Setup
    final String body = readJsonAsStringFromFile("title.json", getClass());
    final Book newBook = webTestClient.post()
        .uri(BOOKS_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .accept()
        .bodyValue(body)
        .exchange().returnResult(Book.class)
        .getResponseBody()
        .blockFirst();

    // Execute
    final WebTestClient.ResponseSpec actualResponseSpec = webTestClient.get()
        .uri(BOOKS_PATH + "/all")
        .exchange();

    // Verify
    final Flux<Book> bookFlux = actualResponseSpec.expectStatus()
        .isOk()
        .returnResult(Book.class)
        .getResponseBody();

    StepVerifier.create(bookFlux)
        .expectNext(newBook)
        .verifyComplete();
  }

  @Test
  void getBooksByPagination() {
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

    // Execute
    final WebTestClient.ResponseSpec actualResponseSpec = webTestClient.get()
        .uri(uri -> uri.path(BOOKS_PATH).queryParam("page", 1)
            .queryParam("size", 1)
            .build())
        .exchange();

    final Flux<Book> bookFlux = actualResponseSpec.expectStatus()
        .isOk()
        .returnResult(Book.class)
        .getResponseBody();

    // Verify
    assertThat(book1).isNotNull();
    assertThat(book2).isNotNull();

    StepVerifier.create(bookFlux)
        .expectNext(book2)
        .verifyComplete();
  }

  @Test
  void getBook() throws IOException {
    // Setup
    final String body = readJsonAsStringFromFile("title.json", getClass());
    final Book newBook = webTestClient.post()
        .uri(BOOKS_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .accept()
        .bodyValue(body)
        .exchange().returnResult(Book.class)
        .getResponseBody()
        .blockFirst();

    // Execute
    final WebTestClient.ResponseSpec actualResponseSpec = webTestClient.get()
        .uri(BOOKS_PATH + "/{id}", newBook.getId())
        .exchange();

    // Verify
    final Flux<Book> bookFlux = actualResponseSpec.expectStatus()
        .isOk()
        .returnResult(Book.class)
        .getResponseBody();

    StepVerifier.create(bookFlux)
        .expectNext(newBook)
        .verifyComplete();
  }

  @Test
  void getBookNotFound() throws IOException {
    // Setup
    final String body = readJsonAsStringFromFile("title.json", getClass());
    final Book newBook = webTestClient.post()
        .uri(BOOKS_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .accept()
        .bodyValue(body)
        .exchange().returnResult(Book.class)
        .getResponseBody()
        .blockFirst();

    // Execute
    final WebTestClient.ResponseSpec actualResponseSpec = webTestClient.get()
        .uri(BOOKS_PATH + "/{id}", 1)
        .exchange();

    // Verify
    actualResponseSpec.expectStatus()
        .isNotFound();
  }

  @Test
  void testDeleteBook() throws IOException {
    // Setup
    final String body = readJsonAsStringFromFile("title.json", getClass());
    final Book newBook = webTestClient.post()
        .uri(BOOKS_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .accept()
        .bodyValue(body)
        .exchange().returnResult(Book.class)
        .getResponseBody()
        .blockFirst();

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

  public static String readJsonAsStringFromFile(final String filename, final Class<?> clazz) throws IOException {
    return IOUtils.toString(readFile(filename, clazz), StandardCharsets.UTF_8);
  }

  public static InputStream readFile(final String filename, final Class<?> clazz) {
    return clazz.getClassLoader().getResourceAsStream(filename);
  }

}