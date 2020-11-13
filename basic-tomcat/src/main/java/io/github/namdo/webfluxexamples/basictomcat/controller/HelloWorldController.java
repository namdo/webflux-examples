package io.github.namdo.webfluxexamples.basictomcat.controller;

import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloWorldController {

  @GetMapping
  Mono<String> sayHelloWorld() {
    return Mono.just("Hello World!");
  }

}
