package com.example.fullstack.user;

import java.util.List;

import org.jboss.resteasy.reactive.ResponseStatus;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/v1/users")
public class UserResource {
    
    @Inject
    private UserService userService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<User>> getAllUsers() {
        return userService.list();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Uni<User> getById(@PathParam("id") long id) {
        return userService.findById(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @ResponseStatus(201)
    @WithTransaction
    public Uni<User> create(User user) {
        return userService.create(user);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Uni<User> update(@PathParam("id") long id, User user) {
        user.id = id;
        return userService.update(user);
    }

    @DELETE
    @Path("{id}")
    public Uni<Void> delete(@PathParam("id") long id) {
        return userService.delete(id);
    }

    @GET
    @Path("self")
    public Uni<User> getCurrentUser() {
        return userService.getCurrentUser();
    }
}
