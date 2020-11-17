package io.github.namdo.webfluxexamples.datamongodb.domain;

import lombok.Builder;
import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
public class Book {

  @Id
  private String id;

  private String title;
}
