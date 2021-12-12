package com.example.datapostgresliquibasetestcontainers.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.data.domain.Pageable;

import com.example.datapostgresliquibasetestcontainers.service.dto.BookDTO;

public interface BookService {

  Mono<BookDTO> save(BookDTO bookDTO);

  Mono<BookDTO> partialUpdate(BookDTO bookDTO);

  Flux<BookDTO> findAll();

  Flux<BookDTO> findAllByPagination(Pageable pageable);

  Mono<Boolean> existsById(Integer id);

  Mono<BookDTO> findOne(Integer id);

  Mono<Void> delete(Integer id);
}
