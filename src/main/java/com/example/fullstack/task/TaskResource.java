package com.example.fullstack.task;

import java.util.List;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/v1")
public class TaskResource {
    
    @GET
    @Path("tasks")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<Task>> getAllTasks() {
        return Task.findAll().list();
    }
}
