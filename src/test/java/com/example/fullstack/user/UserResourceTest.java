package com.example.fullstack.user;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import static org.hamcrest.text.IsEmptyString.emptyString;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;

@QuarkusTest
public class UserResourceTest {

    private static final Logger LOG = Logger.getLogger(UserResourceTest.class);

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
    void createUpdate() {
        var user = given()
                .body("{\"name\":\"foo\",\"password\":\"test\",\"roles\":[\"user\"]}")
                .contentType(ContentType.JSON)
                .when()
                .post("/api/v1/users")
                .as(User.class);

        LOG.info("==INFO==> " + user);
        user.name = "updated";

        given()
                .body(user)
                .contentType(ContentType.JSON)
                .when().put("/api/v1/users/" + user.id)
                .then()
                .statusCode(200)
                .body("name", is("updated"),
                        "version", is(user.version + 1));
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

    @Test
    @TestSecurity(user = "admin", roles = "admin")
    void deleteUser() {
        var toDelete = given()
                .body("{\"name\":\"delete\",\"password\":\"test\",\"roles\":[\"user\"]}")
                .contentType(ContentType.JSON)
                .when()
                .post("/api/v1/users")
                .as(User.class);

        LOG.info("==INFO==> " + toDelete);

        given()
                .when()
                .delete("/api/v1/users/" + toDelete.id)
                .then()
                .statusCode(204);

        // FIXME: assertThat(User.findById(toDelete.id).await().indefinitely(), nullValue());

        given()
                .when()
                .get("/api/v1/users/" + toDelete.id)
                .then()
                .statusCode(404);
    }
}
