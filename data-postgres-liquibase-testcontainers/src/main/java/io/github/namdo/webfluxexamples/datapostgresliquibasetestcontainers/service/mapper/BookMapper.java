package io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.service.mapper;

import io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.domain.Book;
import io.github.namdo.webfluxexamples.datapostgresliquibasetestcontainers.service.dto.BookDTO;

import org.springframework.stereotype.Component;

@Component
public class BookMapper {

  public BookDTO toDto(Book book) {
    return BookDTO.builder()
        .id(book.getId())
        .title(book.getTitle())
        .description(book.getDescription())
        .build();
  }

  public Book toEntity(BookDTO dto) {
    return Book.builder()
        .id(dto.getId())
        .title(dto.getTitle())
        .description(dto.getDescription())
        .build();
  }
}
