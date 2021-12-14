package io.github.namdo.webfluxexamples.client.dto;

import lombok.Builder;
import lombok.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@Value
@Builder(builderClassName = "Builder", toBuilder = true)
@JsonDeserialize(builder = BookDTO.Builder.class)
public class BookDTO {

  Integer id;

  String title;

  String description;

  @JsonPOJOBuilder(withPrefix = "")
  public static class Builder {
  }
}
