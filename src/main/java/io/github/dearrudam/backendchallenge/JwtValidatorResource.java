package io.github.dearrudam.backendchallenge;

import io.github.dearrudam.backendchallenge.beanvalidation.constraints.JWT;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

@Path("/")
@Consumes({MediaType.TEXT_PLAIN})
@Produces({MediaType.TEXT_PLAIN})
public class JwtValidatorResource {

    @Path("/v1/jwt/validate")
    @POST
    public Response validate(String token) {
        if (JwtValidator.isValid(token))
            return Response.ok("verdadeiro").build();
        return Response.status(BAD_REQUEST).entity("falso").build();
    }

    @Path("/v2/jwt/validate")
    @POST
    public Response validateWithBeanValidation(@JWT String token) {
        return Response.ok("verdadeiro").build();
    }


}
