package com.example.fullstack.project;

import org.hibernate.ObjectNotFoundException;

import com.example.fullstack.user.UserService;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ProjectService {

    private final UserService userService;

    @Inject
    public ProjectService(UserService userService) {
        this.userService = userService;
    }
    
    @WithTransaction
    public Uni<Project> findById(long id) {
        return userService.getCurrentUser()
          .chain(user -> Project.<Project>findById(id)
          .onItem().ifNull().failWith(() ->
            new ObjectNotFoundException(id, "Project")));
    }
}
