package com.example.fullstack.user;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.ResponseStatus;

import java.util.List;

@Path("/api/v1/users")
@RolesAllowed("admin")
public class UserResource {

    @Inject
    private UserService userService;

    @GET
    public Uni<List<User>> getAllUsers() {
        return userService.list();
    }

    @GET
    @Path("{id}")
    @WithTransaction
    public Uni<User> getById(@PathParam("id") long id) {
        return userService.findById(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @ResponseStatus(201)
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
    @RolesAllowed("user")
    public Uni<User> getCurrentUser() {
        return userService.getCurrentUser();
    }

    @PUT
    @Path("self/password")
    @RolesAllowed("user")
    public Uni<User> changePassword(PasswordChange passwordChange) {
        return userService
                .changePassword(passwordChange.currentPassword(),
                        passwordChange.newPassword());
    }
}