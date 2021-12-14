package io.github.namdo.webfluxexamples.client.web;

import static io.github.namdo.webfluxexamples.client.utils.Constants.BOOKS_PATH;

import io.github.namdo.webfluxexamples.client.dto.BookDTO;
import io.github.namdo.webfluxexamples.client.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(BOOKS_PATH)
@Log4j2
@RequiredArgsConstructor
public class BookController {

  private final BookService bookService;

  @PostMapping
  public Mono<ResponseEntity<BookDTO>> createBook(@RequestBody final BookDTO book) {
    return bookService.save(book)
        .map(result -> ResponseEntity.status(HttpStatus.CREATED).body(result));
  }

  @PutMapping("/{id}")
  public Mono<ResponseEntity<BookDTO>> updateBook(@PathVariable(value = "id", required = false) final Integer id, @RequestBody final BookDTO book) {
    return bookService.update(book)
        .map(result -> ResponseEntity.ok().body(result));
  }

  @PatchMapping("/{id}")
  public Mono<ResponseEntity<BookDTO>> partialUpdateBook(@PathVariable(value = "id", required = false) final Integer id, @RequestBody final BookDTO book) {
    return bookService.partialUpdate(book)
        .map(result -> ResponseEntity.ok().body(result));
  }

  @GetMapping("/all")
  public Flux<BookDTO> getAllBooks() {
    return bookService.findAll();
  }

  @GetMapping
  public Flux<BookDTO> getBooksByPagination(@RequestParam final int page, @RequestParam final int size) {
    return bookService.findAllByPagination(page, size);
  }

  @GetMapping("/{id}")
  public Mono<ResponseEntity<BookDTO>> getBook(@PathVariable final Integer id) {
    return bookService.findOne(id)
        .map(response -> ResponseEntity.ok().body(response));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public Mono<ResponseEntity<Void>> deleteBook(@PathVariable final Integer id) {
    return bookService.delete(id)
        .map(response -> ResponseEntity.noContent().build());
  }

}
