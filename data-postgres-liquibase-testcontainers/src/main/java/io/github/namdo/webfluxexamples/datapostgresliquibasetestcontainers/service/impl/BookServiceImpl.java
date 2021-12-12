package io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.service.impl;

import static java.util.Objects.nonNull;

import io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.repository.BookRepository;
import io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.service.BookService;
import io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.service.dto.BookDTO;
import io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.service.mapper.BookMapper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {

  private final BookRepository bookRepository;

  private final BookMapper bookMapper;

  @Override
  public Mono<BookDTO> save(final BookDTO bookDTO) {
    return bookRepository.save(bookMapper.toEntity(bookDTO))
        .map(bookMapper::toDto);
  }

  @Override
  public Mono<BookDTO> partialUpdate(BookDTO bookDTO) {
    return bookRepository
        .findById(bookDTO.getId())
        .map(existingBook -> {
          if (nonNull(bookDTO.getTitle())) {
            existingBook.setTitle(bookDTO.getTitle());
          }

          if (nonNull(bookDTO.getDescription())) {
            existingBook.setDescription(bookDTO.getTitle());
          }

          return existingBook;
        })
        .flatMap(bookRepository::save)
        .map(bookMapper::toDto);
  }

  @Override
  @Transactional(readOnly = true)
  public Flux<BookDTO> findAll() {
    return bookRepository.findAll()
        .map(bookMapper::toDto);
  }

  @Override
  @Transactional(readOnly = true)
  public Flux<BookDTO> findAllByPagination(final Pageable pageable) {
    return bookRepository.findAllBy(pageable)
        .map(bookMapper::toDto);
  }

  @Override
  @Transactional(readOnly = true)
  public Mono<Boolean> existsById(Integer id) {
    return bookRepository.existsById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public Mono<BookDTO> findOne(final Integer id) {
    return bookRepository.findById(id)
        .map(bookMapper::toDto);
  }

  @Override
  public Mono<Void> delete(final Integer id) {
    return bookRepository.deleteById(id);
  }

}
