package io.github.namdo.webfluxexamples.datamysql.utils;

import io.r2dbc.spi.ConnectionFactory;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

@TestConfiguration
public class DatabaseInitializer {

  @Bean
  public ConnectionFactoryInitializer initializer(final ConnectionFactory connectionFactory) {

    final ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
    initializer.setConnectionFactory(connectionFactory);

    final CompositeDatabasePopulator populator = new CompositeDatabasePopulator();
    populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));
    initializer.setDatabasePopulator(populator);

    return initializer;
  }
}
