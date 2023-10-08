package com.redhat.quarkus.routes;

import com.redhat.quarkus.model.MoveLog;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

public class InFloorRoute extends RouteBuilder {

  @Override
  public void configure() throws Exception {
    from("direct:updateFloorData")
        .to("micrometer:timer:updateFloorDataTimer?action=start")
        .unmarshal().json(JsonLibrary.Jackson, MoveLog.class)
        .log(
            "InFloorRoute: Redirecting MoveLog data \"${body}\" for destination floor ${body.destination} to Database."
            )
        .process(exchange -> {
          MoveLog moveLog = exchange.getIn().getBody(MoveLog.class);
          int destinationFloor = Integer.valueOf(moveLog.getDestination());
          exchange.getIn().setHeader("floorNumber", destinationFloor);
        })
        .setBody(simple(
            "UPDATE FloorData SET people_count = LEAST(people_count + 1, max_people) WHERE floor_number = ${header[floorNumber]}"))
        .to("jdbc:building-ds")
        .to("micrometer:timer:updateFloorDataTimer?action=stop")
        .to("micrometer:counter:updateFloorData");
  }
}
