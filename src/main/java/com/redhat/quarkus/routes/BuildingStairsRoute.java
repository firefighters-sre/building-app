package com.redhat.quarkus.routes;

import org.apache.camel.builder.RouteBuilder;

public class BuildingStairsRoute extends RouteBuilder {

  @Override
    public void configure() throws Exception {
        from("kafka:{{kafka.topic.stairs.name}}")
          .log("Received from Stairs ${body}") 
          .to("micrometer:counter:stairsCounter")
          .to("direct:updateFloorData");
    }
}
