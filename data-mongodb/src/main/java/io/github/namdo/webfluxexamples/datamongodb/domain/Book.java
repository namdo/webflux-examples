package io.github.namdo.webfluxexamples.datamongodb.domain;

import lombok.Builder;
import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@Data
@Builder(builderClassName = "Builder")
@Document
@JsonDeserialize(builder = Book.Builder.class)
public class Book {

  @Id
  private String id;

  private String title;

  private String description;

  @JsonPOJOBuilder(withPrefix = "")
  public static class Builder {
  }
}
