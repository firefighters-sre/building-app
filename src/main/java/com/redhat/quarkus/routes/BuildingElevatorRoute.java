package com.redhat.quarkus.routes;

import org.apache.camel.builder.RouteBuilder;

public class BuildingElevatorRoute extends RouteBuilder {

  @Override
    public void configure() throws Exception {
        from("kafka:{{kafka.topic.elevator.name}}")
          .log("Received from Elevator ${body}") 
          .to("micrometer:counter:elevatorCounter")
          .to("direct:updateFloorData");
    }
}
