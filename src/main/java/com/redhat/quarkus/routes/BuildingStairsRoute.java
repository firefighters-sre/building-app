package com.redhat.quarkus.routes;

import org.apache.camel.builder.RouteBuilder;

public class BuildingStairsRoute extends RouteBuilder {

  @Override
    public void configure() throws Exception {
        from("kafka:{{kafka.topic.stairs.name}}")
          .log("Redirect \"${body}\" from Stairs")
          .to("direct:updateFloorData");
    }
}
