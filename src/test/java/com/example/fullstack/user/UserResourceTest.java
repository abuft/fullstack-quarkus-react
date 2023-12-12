package com.example.fullstack.user;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import static org.hamcrest.text.IsEmptyString.emptyString;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class UserResourceTest {

    private static final Logger LOG = Logger.getLogger(UserResourceTest.class);

    @Test
    @TestSecurity(user = "admin", roles = "admin")
    void listUser() {
        given()
                .when()
                .get("/api/v1/users")
                .then()
                .statusCode(200)
                .body("$.size()", greaterThanOrEqualTo(1),
                        "[0].name", is(not(emptyString())),
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

        given()
                .when()
                .get("/api/v1/users/" + toDelete.id)
                .then()
                .statusCode(404);

        /*
        FIXME: reactive testing changes in Quarkus 3
               Migration-Guide: https://github.com/quarkusio/quarkus/wiki/Migration-Guide-3.0#hibernate-reactive-panache
        assertThat(User.findById(toDelete.id).await().indefinitely(), nullValue());
         */
    }

    @Test
    @TestSecurity(user = "admin", roles = "admin")
    void updateOptimisticLock() {
        given()
                .body("{\"name\":\"updated\",\"password\":\"test\",\"version\":\"1337\"}")
                .contentType(ContentType.JSON)
                .when()
                .put("/api/v1/users/0")
                .then()
                .statusCode(409);
    }

    @Test
    @TestSecurity(user = "admin", roles = "user")
    void changePassword() {
        given()
                .body("{\"currentPassword\":\"quarkus\", \"newPassword\":\"changed\"}")
                .contentType(ContentType.JSON)
                .when()
                .put("/api/v1/users/self/password")
                .then()
                .statusCode(200);

        /*
        FIXME: reactive testing changes in Quarkus 3. Error No currenct Vertx context found
               Migration-Guide: https://github.com/quarkusio/quarkus/wiki/Migration-Guide-3.0#hibernate-reactive-panache
        assertTrue(BcryptUtil.matches("changed",
                User.<User>findById(0L).await().indefinitely().password));
         */
    }
}
