package com.example.fullstack.auth;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.not;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class AuthResourceTest {

    /*
     * Like: curl -XPOST
     *            -d"{\"name\":\"admin\",\"password\":\"quarkus\"}"
     *            -H "Content-Type: application/json"
     *            localhost:8080/api/v1/auth/login
     *
     * $ mvn clean test -Dtest=AuthResourceTest
     */
    @Test
    void loginWithValidCredentials() {
        given()
                .body("{\"name\":\"admin\",\"password\":\"quarkus\"}")
                .contentType(ContentType.JSON)
                .when().post("/api/v1/auth/login")
                .then()
                .statusCode(200)
                .body(not(emptyString()));
    }

    @Test
    void loginWithInvalidCredentials() {
        given()
                .body("{\"name\":\"admin\",\"password\":\"invalid password\"}")
                .contentType(ContentType.JSON)
                .when().post("/api/v1/auth/login")
                .then()
                .statusCode(401);
    }

}
