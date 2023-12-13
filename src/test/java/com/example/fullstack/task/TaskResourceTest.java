package com.example.fullstack.task;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class TaskResourceTest {

    @Test
    @TestSecurity(user = "user", roles = "user")
    void listTasks() {
        given()
                .when()
                .get("/api/v1/tasks")
                .then()
                .statusCode(200)
                .body("$.size()", greaterThanOrEqualTo(0));
    }

    @Test
    @TestSecurity(user = "admin", roles = "user")
    void createTask() {
        var toCreate = given()
                .body("{\"title\":\"to-create\"}")
                .contentType(ContentType.JSON)
                .when()
                .post("/api/v1/tasks")
                .as(Task.class);

        given()
                .when()
                .get("/api/v1/tasks")
                .then()
                .statusCode(200)
                .body("[0].title", is("to-create"));

    }

    @Test
    @TestSecurity(user = "user", roles = "user")
    void updateTask() {

    }

    @Test
    @TestSecurity(user = "user", roles = "user")
    void deleteTask() {

    }

    @Test
    @TestSecurity(user = "user", roles = "user")
    void setTaskComplete() {
        var toSetComplete = given()
                .body("{\"title\":\"to-set-complete\"}")
                .contentType(ContentType.JSON)
                .when()
                .post("/api/v1/tasks")
                .as(Task.class);

        given()
                .body("\"true\"")
                .contentType(ContentType.JSON)
                .when()
                .put("/api/v1/tasks/1/complete")
                .then()
                .statusCode(200);

    }
}
