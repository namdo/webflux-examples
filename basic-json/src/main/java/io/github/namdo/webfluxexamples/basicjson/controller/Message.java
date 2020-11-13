package io.github.namdo.webfluxexamples.basicjson.controller;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Message {

  public String content;

}
