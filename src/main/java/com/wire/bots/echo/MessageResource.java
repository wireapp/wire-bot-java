package com.wire.bots.echo;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.MDC;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Api
@Path("/ok")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MessageResource {

    @GET
    @ApiResponses(value = {@ApiResponse(code = 200, message = "All good")})
    public Response get() {
        MDC.put("app_request", "APP_REQUEST");
        MDC.put("infra_request", "INFRA");

        return Response
                .ok()
                .build();
    }
}
