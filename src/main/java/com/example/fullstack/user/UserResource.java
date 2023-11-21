package com.example.fullstack.user;

import java.util.List;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/v1")
public class UserResource {
    
    @GET
    @Path("users")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<User>> getAllUsers() {
        return User.findAll().list();
    }
}
