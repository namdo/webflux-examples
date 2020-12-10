package io.github.namdo.webfluxexamples.datamysql.domain;

import lombok.Builder;
import lombok.Data;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@Data
@Builder(builderClassName = "Builder")
@JsonDeserialize(builder = Book.Builder.class)
public class Book {

  @Id
  private Integer id;

  private String title;

  @JsonPOJOBuilder(withPrefix = "")
  public static class Builder {
  }
}
