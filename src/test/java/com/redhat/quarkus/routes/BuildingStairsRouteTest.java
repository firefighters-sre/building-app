package com.redhat.quarkus.routes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

import com.redhat.quarkus.model.MoveLog;

import io.agroal.api.AgroalDataSource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.quarkus.test.CamelQuarkusTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
public class BuildingStairsRouteTest extends CamelQuarkusTestSupport {

  @Inject
  AgroalDataSource ds;

  @Inject
  ProducerTemplate producerTemplate;

  @Inject
  CamelContext context;

  @Override
  protected CamelContext createCamelContext() throws Exception {
    return this.context;
  }

  @BeforeEach
  void setupDatabase() throws Exception {
    try (Connection con = ds.getConnection()) {
      try (Statement statement = con.createStatement()) {
        con.setAutoCommit(true);
        // Reset the FloorData table
        statement.execute("DROP TABLE IF EXISTS FloorData");
        statement.execute(
            "CREATE TABLE FloorData (" +
                "floor_number INT PRIMARY KEY, " +
                "people_count INT NOT NULL DEFAULT 0, " +
                "structure_quality INT CHECK (structure_quality BETWEEN 1 AND 5) NOT NULL, " +
                "max_people INT NOT NULL, " +
                "o2_level DECIMAL(4,1) NOT NULL, " +
                "co2_level DECIMAL(4,2) NOT NULL" +
                ")");
        // Initialize the 5 floors with the given parameters
        for (int i = 1; i <= 5; i++) {
          statement.execute(
              "INSERT INTO FloorData (floor_number, people_count, structure_quality, max_people, o2_level, co2_level) "
                  +
                  "VALUES (" + i + ", 0, 5, 30, 20.9, 0.04)" // Assuming O2 level as 20.9% and CO2
                                                             // level as 0.04% as common atmospheric
                                                             // levels
          );
        }
      }
    }
  }

  @AfterEach
  void tearDownDatabase() throws Exception {
    try (Connection con = ds.getConnection()) {
      try (Statement statement = con.createStatement()) {
        // Reset the FloorData table
        statement.execute("DROP TABLE IF EXISTS FloorData");
      }
    }
  }

  @Test
  void testFloorsInitialization() throws Exception {
    // Expected values
    final int expectedMaxPeople = 30;
    final int expectedStructureQuality = 5;
    final double expectedO2Level = 20.9;
    final double expectedCO2Level = 0.04;

    try (Connection con = ds.getConnection()) {
      try (Statement stmt = con.createStatement()) {
        for (int i = 1; i <= 5; i++) {
          try (ResultSet rs = stmt.executeQuery("SELECT * FROM FloorData WHERE floor_number = " + i)) {
            assertTrue(rs.next(), "Should have found a row for floor " + i);

            // Assert max_people
            assertEquals(expectedMaxPeople, rs.getInt("max_people"),
                "Unexpected max_people for floor " + i);

            // Assert structure_quality
            assertEquals(expectedStructureQuality, rs.getInt("structure_quality"),
                "Unexpected structure_quality for floor " + i);

            // Assert o2_level
            assertEquals(expectedO2Level, rs.getDouble("o2_level"), 0.01,
                "Unexpected o2_level for floor " + i);

            // Assert co2_level
            assertEquals(expectedCO2Level, rs.getDouble("co2_level"), 0.01,
                "Unexpected co2_level for floor " + i);
          }
        }
      }
    }
  }

  @Test
  void testConsumeFromStairs() throws Exception {

    String moveLog = "{\"personId\":1,\"destination\":\"1\",\"preferredRoute\":\"stairs\"}";
    final String expectedQuery = "UPDATE FloorData SET people_count = LEAST(people_count + 1, max_people) WHERE floor_number = 1";
    final String unexpectedQuery = "UPDATE FloorData SET people_count = LEAST(people_count + 1, max_people) WHERE floor_number = ${header[floorNumber]}";

    final MockEndpoint mockFloorData = getMockEndpoint("mock:updateFloorData");
    mockFloorData.expectedMessageCount(1);

    // Send a message to the route
    final Exchange exchange = this.createExchangeWithBody(moveLog);

    this.producerTemplate.send("direct:{{kafka.topic.stairs.name}}", exchange);

    mockFloorData.assertIsSatisfied();

    final MockEndpoint mockBuildingDS = getMockEndpoint("mock:building-ds");
    mockBuildingDS.expectedMessageCount(1);

    this.producerTemplate.send("direct:updateFloorDataTest", exchange);

    mockBuildingDS.assertIsSatisfied();

    assertEquals(1, mockBuildingDS.getExchanges().get(0).getMessage().getHeader("floorNumber"));
    assertNotEquals(5, mockBuildingDS.getExchanges().get(0).getMessage().getHeader("floorNumber"));
    assertEquals(expectedQuery, mockBuildingDS.getExchanges().get(0).getMessage().getBody());
    assertNotEquals(unexpectedQuery, mockBuildingDS.getExchanges().get(0).getMessage().getBody());

    // Assert: Inspect the database to ensure the query had the desired effect
    try (Connection con = ds.getConnection()) {
      try (Statement stmt = con.createStatement()) {
        stmt.execute(mockBuildingDS.getExchanges().get(0).getMessage().getBody().toString());
        try (ResultSet rs = stmt.executeQuery("SELECT * FROM FloorData WHERE floor_number = 1")) {
          assertTrue(rs.next(), "Should have found a row for floor 1");
          assertEquals(1, rs.getInt("people_count"), "Should have incremented people_count for floor 1");
          assertNotEquals(rs.getInt("floor_number"), 2, "Should have incremented people_count for floor 1");
        }
      }
    }
  }

  @Override
  protected RouteBuilder createRouteBuilder() {
    return new RouteBuilder() {
      @Override
      public void configure() throws Exception {

        from("direct:{{kafka.topic.stairs.name}}")
            .log("Received from Stairs ${body}")
            .to("mock:updateFloorData");

        from("direct:updateFloorDataTest")
            .unmarshal().json(JsonLibrary.Jackson, MoveLog.class)
            .log(
                "InFloorRoute: Redirecting MoveLog data \"${body}\" for destination floor ${body.destination} to Database.")
            .process(exchange -> {
              MoveLog moveLog = exchange.getIn().getBody(MoveLog.class);
              int destinationFloor = Integer.valueOf(moveLog.getDestination());
              exchange.getIn().setHeader("floorNumber", destinationFloor);
            })
            .setBody(simple(
                "UPDATE FloorData SET people_count = LEAST(people_count + 1, max_people) WHERE floor_number = ${header[floorNumber]}"))
            .to("mock:building-ds");
      }
    };
  }
}