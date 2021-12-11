package io.github.namdo.webfluxexamples.datapgr2dbc.repository;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.namdo.webfluxexamples.datapgr2dbc.domain.Book;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@DataR2dbcTest
@Log4j2
@Testcontainers
class BookRepositoryTest {

  @Container
  public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres").withTag("14.1"));

  @DynamicPropertySource
  static void postgresProperties(final DynamicPropertyRegistry registry) {
    registry.add("spring.r2dbc.url", () -> "r2dbc:postgresql://"
        + postgreSQLContainer.getHost() + ":" + postgreSQLContainer.getFirstMappedPort()
        + "/" + postgreSQLContainer.getDatabaseName());
    registry.add("spring.r2dbc.username", () -> postgreSQLContainer.getUsername());
    registry.add("spring.r2dbc.password", () -> postgreSQLContainer.getPassword());
  }

  private final Integer COUNT = 10;

  private final String TITLE = "New Book";

  @Autowired
  private BookRepository bookRepository;

  @BeforeEach
  public void setUp() {
    bookRepository.deleteAll().thenMany(Flux.range(1, COUNT).map(i -> Book.builder()
                .title("A Book " + i)
                .build())
            .flatMap(bookRepository::save))
        .collectList().block();
  }

  @Test
  void testSave() {
    // Setup
    final Book book = Book.builder().title(TITLE).build();

    // Execute
    final Mono<Book> bookMono = bookRepository.save(book);

    // Verify
    StepVerifier.create(bookMono)
        .consumeNextWith(b -> {
          assertThat(b).isNotNull();
          assertThat(b.getTitle()).isEqualTo(TITLE);
          assertThat(b.getId()).isNotNull();
        })
        .verifyComplete();
  }

  @Test
  void testSaveAndFindByTitle() {
    // Setup
    final Book book = Book.builder().title(TITLE).build();

    // Execute
    final Flux<Book> bookFlux = bookRepository.save(book)
        .flatMapMany(b -> bookRepository.findAllByTitleEquals(TITLE));

    // Verify
    StepVerifier.create(bookFlux)
        .consumeNextWith(b -> assertThat(b.getTitle()).isEqualTo(TITLE))
        .verifyComplete();
  }

  @Test
  void testFindAll() {
    // Execute
    final Flux<Book> bookFlux = bookRepository.findAll();

    // Verify
    StepVerifier.create(bookFlux)
        .expectNextCount(COUNT)
        .verifyComplete();
  }

  @Test
  void testFindOne() {
    // Setup
    final Book book = Book.builder().title(TITLE).build();

    final Book expected = bookRepository.save(book)
        .block();

    // Execute
    final Book actual = bookRepository.findById(expected.getId()).block();

    // Verify
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void testDelete() {
    // Setup
    final String TITLE = "New Book";

    final Book expected = bookRepository.save(Book.builder().title(TITLE).build())
        .block();

    final Long firstCount = bookRepository.count().block();

    // Execute
    bookRepository.delete(expected).block();

    // Verify
    assertThat(firstCount).isEqualTo((long) COUNT + 1);
    assertThat(bookRepository.count().block()).isEqualTo((long) COUNT);
  }
}
