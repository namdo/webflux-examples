package io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.web;

import static io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.utils.Constants.BOOKS_PATH;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.Objects;

import io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.service.BookService;
import io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.service.dto.BookDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(BOOKS_PATH)
@Log4j2
@RequiredArgsConstructor
public class BookController {

  private final BookService bookService;

  @PostMapping
  public Mono<ResponseEntity<BookDTO>> createBook(@RequestBody final BookDTO book) {
    if (nonNull(book.getId())) {
      return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
    }
    return bookService.save(book)
        .map(result -> ResponseEntity.status(HttpStatus.CREATED).body(result));
  }

  @PutMapping("/{id}")
  public Mono<ResponseEntity<BookDTO>> updateBook(@PathVariable(value = "id", required = false) final Integer id, @RequestBody final BookDTO book) {
    if (isNull(book.getId())) {
      return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
    }
    if (!Objects.equals(id, book.getId())) {
      return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
    }

    return bookService
        .existsById(id)
        .flatMap(exists -> {
          if (Boolean.FALSE.equals(exists)) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
          }

          return bookService
              .save(book)
              .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
              .map(result ->
                  ResponseEntity
                      .ok()
                      .body(result)
              );
        });
  }

  @PatchMapping("/{id}")
  public Mono<ResponseEntity<BookDTO>> partialUpdateBook(@PathVariable(value = "id", required = false) final Integer id, @RequestBody final BookDTO book) {
    if (isNull(book.getId())) {
      return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
    }
    if (!Objects.equals(id, book.getId())) {
      return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
    }

    return bookService
        .existsById(id)
        .flatMap(exists -> {
          if (Boolean.FALSE.equals(exists)) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
          }

          Mono<BookDTO> result = bookService.partialUpdate(book);

          return result
              .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
              .map(res ->
                  ResponseEntity
                      .ok()
                      .body(res)
              );
        });
  }

  @GetMapping("/all")
  public Flux<BookDTO> getAllBooks() {
    return bookService.findAll();
  }

  @GetMapping
  public Flux<BookDTO> getBooksByPagination(@RequestParam final int page, @RequestParam final int size) {
    return bookService.findAllByPagination(PageRequest.of(page, size));
  }

  @GetMapping("/{id}")
  public Mono<ResponseEntity<BookDTO>> getBook(@PathVariable final Integer id) {
    return bookService.findOne(id)
        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
        .map(response -> ResponseEntity.ok().body(response));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public Mono<ResponseEntity<Void>> deleteBook(@PathVariable final Integer id) {
    return bookService.delete(id)
        .map(response -> ResponseEntity.noContent().build());
  }

}
