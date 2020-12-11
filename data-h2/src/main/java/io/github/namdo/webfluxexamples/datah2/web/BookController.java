package io.github.namdo.webfluxexamples.datah2.web;

import static io.github.namdo.webfluxexamples.datah2.utils.DataMongodbConstants.BOOKS_PATH;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import io.github.namdo.webfluxexamples.datah2.domain.Book;
import io.github.namdo.webfluxexamples.datah2.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(BOOKS_PATH)
@Log4j2
@RequiredArgsConstructor
public class BookController {

  private final BookService bookService;

  @PostMapping
  @ResponseStatus(code = HttpStatus.CREATED)
  public Mono<Book> createBook(@RequestBody final Book book) {
    if (nonNull(book.getId())) {
      return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
    }
    return bookService.save(book);
  }

  @PutMapping
  @ResponseStatus(code = HttpStatus.CREATED)
  public Mono<Book> updateBook(@RequestBody final Book book) {
    if (isNull(book.getId())) {
      return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
    }
    return bookService.save(book)
        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
  }

  @GetMapping("/all")
  public Flux<Book> getAllBooks() {
    return bookService.findAll();
  }

  @GetMapping
  public Flux<Book> getBooksByPagination(@RequestParam final int page, @RequestParam final int size) {
    return bookService.findAllByPagination(PageRequest.of(page, size));
  }

  @GetMapping("/{id}")
  public Mono<Book> getBook(@PathVariable final Integer id) {
    return bookService.findOne(id)
        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public Mono<Void> deleteBook(@PathVariable final Integer id) {
    return bookService.delete(id);
  }

}
