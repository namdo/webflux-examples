package io.github.namdo.webfluxexamples.client.service.impl;

import io.github.namdo.webfluxexamples.client.client.BookServiceClient;
import io.github.namdo.webfluxexamples.client.dto.BookDTO;
import io.github.namdo.webfluxexamples.client.service.BookService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

  private final BookServiceClient bookServiceClient;

  @Override
  public Mono<BookDTO> save(BookDTO bookDTO) {
    return bookServiceClient.save(bookDTO);
  }

  @Override
  public Mono<BookDTO> update(BookDTO bookDTO) {
    return bookServiceClient.update(bookDTO);
  }

  @Override
  public Mono<BookDTO> partialUpdate(BookDTO bookDTO) {
    return bookServiceClient.patch(bookDTO);
  }

  @Override
  public Flux<BookDTO> findAll() {
    return bookServiceClient.findAll();
  }

  @Override
  public Flux<BookDTO> findAllByPagination(int page, int size) {
    return bookServiceClient.findAllByPagination(page, size);
  }

  @Override
  public Mono<BookDTO> findOne(Integer id) {
    return bookServiceClient.findOne(id);
  }

  @Override
  public Mono<Void> delete(Integer id) {
    return bookServiceClient.delete(id);
  }
}
