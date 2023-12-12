package com.example.fullstack.project;

import com.example.fullstack.task.Task;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@QuarkusTest
public class ProjectResourceTest {

    @Test
    @TestSecurity(user = "user", roles = "user")
    void listProject() {
        given()
                .when()
                .get("/api/v1/projects")
                .then()
                .statusCode(200)
                .body("$.size()", greaterThanOrEqualTo(1),
                        "[0].id", is(0),
                        "[0].name", is("Work"),
                        "[0].user.roles[0]", is("user"),
                        "version", not(emptyString())
                );
    }

    @Test
    @TestSecurity(user = "user", roles = "user")
    void createProject() {
        given()
                .body("{\"name\":\"test\", \"user\":{\"name\":\"test\",\"password\":\"test\"}}")
                .contentType(ContentType.JSON)
                .when()
                .post("/api/v1/projects")
                .then()
                .statusCode(201);
    }

    @Test
    @TestSecurity(user = "user", roles = "user")
    void updateProject() {
        var project = given()
                .body("{\"name\":\"to-update\", \"user\":{\"name\":\"test\",\"password\":\"test\"}}")
                .contentType(ContentType.JSON)
                .when()
                .post("/api/v1/projects")
                .as(Project.class);

        project.name = "updated";

        given()
                .body(project)
                .contentType(ContentType.JSON)
                .when()
                .put("/api/v1/projects/" + project.id)
                .then()
                .statusCode(200)
                .body("name", is("updated"),
                        "version", is(project.version + 1));

    }

    @Test
    @TestSecurity(user = "user", roles = "user")
    void deleteProject() {
        var toDelete = given()
                .body("{\"name\":\"to-update\"}")
                .contentType(ContentType.JSON)
                .when()
                .post("/api/v1/projects")
                .as(Project.class);

        var dependentTask = given()
                .body("{\"title\":\"dependent-task\", \"project\":{\"id\":" + toDelete.id + "}}")
                .contentType(ContentType.JSON)
                .post("/api/v1/tasks")
                .as(Task.class);

        given()
                .when()
                .delete("/api/v1/projects/" + toDelete.id)
                .then()
                .statusCode(204);
    }

}
