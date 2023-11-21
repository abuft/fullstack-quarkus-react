package com.example.fullstack.project;

import java.util.List;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/v1")
public class ProjectResource {

    @GET
    @Path("projects")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<Project>> getAllProjects() {
        return Project.findAll().list();
    }
    
}
