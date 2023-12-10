package com.example.fullstack.task;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

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
}
