package com.example.fullstack.user;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;

@QuarkusTest
public class UserResourceTest {

    @Test
    @TestSecurity(user = "admin", roles = "admin")
    void listAdminUser() {
        given()
                .when()
                .get("/api/v1/users")
                .then()
                .statusCode(200)
                .body("$.size()", greaterThanOrEqualTo(1),
                        "[0].name", is("admin"),
                        "[0].password", nullValue());
    }

    @Test
    @TestSecurity(user = "admin", roles = "admin")
    void createNewUser() {
        given()
                .body("{\"name\":\"test\",\"password\":\"test\",\"roles\":[\"user\"]}")
                .contentType(ContentType.JSON)
                .when()
                .post("/api/v1/users")
                .then()
                .statusCode(201)
                .body("name", is("test"),
                        "password", nullValue(),
                        "created", not(emptyString()));
    }

    @Test
    @TestSecurity(user = "user", roles = "user")
    void createUnauthorized() {
        given()
                .body("{\"name\":\"test-unauthorized\",\"password\":\"test\",\"roles\":[\"user\"]}")
                .contentType(ContentType.JSON)
                .when()
                .post("/api/v1/users")
                .then()
                .statusCode(403);
    }

    @Test
    @TestSecurity(user = "admin", roles = "admin")
    void createDuplicate() {
        given()
                .body("{\"name\":\"user\",\"password\":\"quarkus\",\"roles\":[\"user\"]}")
                .contentType(ContentType.JSON)
                .when()
                .post("/api/v1/users")
                .then()
                .statusCode(409);
    }
}
