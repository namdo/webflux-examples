package com.example.datapostgresliquibasetestcontainers.service.mapper;

import org.springframework.stereotype.Component;

import com.example.datapostgresliquibasetestcontainers.domain.Book;
import com.example.datapostgresliquibasetestcontainers.service.dto.BookDTO;

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
