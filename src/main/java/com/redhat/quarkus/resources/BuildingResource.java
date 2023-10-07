package com.redhat.quarkus.resources;

import java.sql.Connection;
import java.sql.Statement;

import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.apache.camel.CamelContext;

@ApplicationScoped
public class BuildingResource {
  
  @Inject
  @DataSource("building-ds")
  AgroalDataSource dataSource;

  void startup(@Observes StartupEvent event, CamelContext context) throws Exception {
      try (Connection con = dataSource.getConnection()) {
          try (Statement statement = con.createStatement()) {
              con.setAutoCommit(true);
              statement.execute("DROP TABLE IF EXISTS FloorData");
              statement.execute(
                      "CREATE TABLE FloorData (" +
                              "floor_number INT PRIMARY KEY, " +
                              "people_count INT NOT NULL DEFAULT 0, " +
                              "structure_quality INT CHECK (structure_quality BETWEEN 1 AND 5) NOT NULL, " +
                              "max_people INT NOT NULL, " +
                              "o2_level DECIMAL NOT NULL, " +
                              "co2_level DECIMAL NOT NULL" +
                              ")"
              );
          }
      }
  }
}






