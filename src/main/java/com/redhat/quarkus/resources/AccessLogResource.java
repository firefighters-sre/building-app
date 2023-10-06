package com.redhat.quarkus.resources;

import com.redhat.quarkus.model.AccessLog;
import com.redhat.quarkus.service.AccessLogService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/accesslog")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccessLogResource {

    @Inject
    AccessLogService accessLogService;

}
