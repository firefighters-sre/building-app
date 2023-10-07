package com.redhat.quarkus.routes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.redhat.quarkus.model.MoveLog;

import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
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
public class BuildingElevatorRouteTest extends CamelQuarkusTestSupport {

  // @Inject
  // AgroalDataSource ds;

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
   //
  }

  @AfterEach
  void tearDownDatabase() {
    // Code to tear down your database.
  }

  @Test
  void testConsumeFromElevator() throws Exception {

    String moveLog = "{\"personId\":1,\"destination\":\"1\",\"preferredRoute\":\"elevator\"}";
    final String expectedQuery = "UPDATE FloorData SET people_count = LEAST(people_count + 1, max_people) WHERE floor_number = 1";
    final String unexpectedQuery = "UPDATE FloorData SET people_count = LEAST(people_count + 1, max_people) WHERE floor_number = ${header[floorNumber]}";

    final MockEndpoint mockFloorData = getMockEndpoint("mock:updateFloorData");
    mockFloorData.expectedMessageCount(1);

    // Send a message to the route
    final Exchange exchange = this.createExchangeWithBody(moveLog);

    this.producerTemplate.send("direct:{{kafka.topic.elevator.name}}", exchange);

    mockFloorData.assertIsSatisfied();

    final MockEndpoint mockBuildingDS = getMockEndpoint("mock:building-ds");
    mockBuildingDS.expectedMessageCount(1);

    this.producerTemplate.send("direct:updateFloorDataTest", exchange);

    mockBuildingDS.assertIsSatisfied();

    assertEquals(1, mockBuildingDS.getExchanges().get(0).getMessage().getHeader("floorNumber"));
    assertNotEquals(5, mockBuildingDS.getExchanges().get(0).getMessage().getHeader("floorNumber"));
    assertEquals(expectedQuery, mockBuildingDS.getExchanges().get(0).getMessage().getBody());
    assertNotEquals(unexpectedQuery, mockBuildingDS.getExchanges().get(0).getMessage().getBody());

    // Arrange: Ensure the database is in a known state
    // try (Connection con = this.ds.getConnection()) {
    //   try (Statement stmt = con.createStatement()) {
    //       stmt.execute("INSERT INTO FloorData (floor_number, people_count, structure_quality, max_people, o2_level, co2_level) VALUES (1, 0, 5, 100, 20.9, 0.04)");
    //   }
    // }

  }

  @Override
  protected RouteBuilder createRouteBuilder() {
    return new RouteBuilder() {
      @Override
      public void configure() throws Exception {

        from("direct:{{kafka.topic.elevator.name}}")
            .to("mock:updateFloorData");

        from("direct:updateFloorDataTest")
            .unmarshal().json(JsonLibrary.Jackson, MoveLog.class)
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