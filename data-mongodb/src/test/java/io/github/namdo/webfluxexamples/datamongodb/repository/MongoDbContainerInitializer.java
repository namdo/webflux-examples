package io.github.namdo.webfluxexamples.datamongodb.repository;

import lombok.extern.log4j.Log4j2;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

@Log4j2
class MongoDbContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
  @Override
  public void initialize(final ConfigurableApplicationContext configurableApplicationContext) {
    final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo").withTag("4.0.10"));
    mongoDBContainer.start();

    configurableApplicationContext
        .addApplicationListener((ApplicationListener<ContextClosedEvent>) event -> mongoDBContainer.stop());
    log.debug("mongoDBContainer.getReplicaSetUrl():" + mongoDBContainer.getReplicaSetUrl());
    TestPropertyValues
        .of("spring.data.mongodb.uri=" + mongoDBContainer.getReplicaSetUrl())
        .applyTo(configurableApplicationContext.getEnvironment());
  }
}
