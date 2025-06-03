package com.crm.auth.controller;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import static com.crm.sharedlib.consts.CrmConstants.USER_ID_HEADER_NAME;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@Sql(scripts = "classpath:sql/insertTestUsers.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:sql/deleteTestUsers.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class UserControllerTest extends BaseIntegrationTest {

    private final String BASE_URI = "/api/users";

    @Test
    @DisplayName("Get authenticated user expected success response")
    public void getAuthenticatedUserExpectedSuccess() {

        final int userId = 101;

        given()
                .contentType(ContentType.JSON)
                .header(USER_ID_HEADER_NAME, userId)
                .when()
                .get(BASE_URI + "/me")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(userId))
                .body("login", notNullValue())
                .body("email", notNullValue())
                .body("fullName", is("Serious Sam"))
                .body("firstName", is("Serious"))
                .body("lastName", is("Sam"));
    }

    @Test
    @DisplayName("Get authenticated user when user is not exists expected not found response")
    public void getAuthenticatedUserWhenUserNotFoundExpectedNotFound() {

        final int userId = 200;

        given()
                .contentType(ContentType.JSON)
                .header(USER_ID_HEADER_NAME, userId)
                .when()
                .get(BASE_URI + "/me")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", is("User is not found"));
    }

    @Test
    @DisplayName("Get user by Full Name expected success response")
    public void getUserByFullNameExpectedSuccess() {

        final int userId = 101;

        given()
                .contentType(ContentType.JSON)
                .header(USER_ID_HEADER_NAME, userId)
                .param("fullName", "Serious Sam")
                .when()
                .get(BASE_URI)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("[0].id", is(userId))
                .body("[0].login", notNullValue())
                .body("[0].email", notNullValue())
                .body("[0].fullName", is("Serious Sam"))
                .body("[0].firstName", is("Serious"))
                .body("[0].lastName", is("Sam"));
    }

}