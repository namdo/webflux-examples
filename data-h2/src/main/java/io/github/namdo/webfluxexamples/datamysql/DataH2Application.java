package io.github.namdo.webfluxexamples.datamysql;

import io.r2dbc.spi.ConnectionFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

@SpringBootApplication
public class DataH2Application {

  public static void main(final String[] args) {
    SpringApplication.run(DataH2Application.class, args);
  }

  @Bean
  ConnectionFactoryInitializer initializer(final ConnectionFactory connectionFactory) {

    final ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
    initializer.setConnectionFactory(connectionFactory);
    initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));

    return initializer;
  }

}
