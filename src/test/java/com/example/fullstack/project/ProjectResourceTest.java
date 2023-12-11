package com.example.fullstack.project;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
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
}
