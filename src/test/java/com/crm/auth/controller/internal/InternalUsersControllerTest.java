package com.crm.auth.controller.internal;

import com.crm.auth.BaseIntegrationTest;
import com.crm.sharedlib.dto.DateRange;
import com.crm.sharedlib.dto.request.UserWithRolesFilterRequest;
import com.crm.sharedlib.dto.response.UserAndRoles;
import com.crm.sharedlib.enums.SortDirections;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import static com.crm.sharedlib.consts.CrmConstants.USER_ID_HEADER_NAME;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Sql(scripts = {
        "classpath:sql/insertTestUsers.sql",
        "classpath:sql/insertTestRoles.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:sql/deleteTestUsers.sql",
        "classpath:sql/deleteTestRoles.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class InternalUsersControllerTest extends BaseIntegrationTest {

    private final String BASE_URI = "/api/internal/users";

    @Test
    @DisplayName("Filter users of organization with simple filter request expected success")
    public void filterUsersOfOrganizationExpectedSuccess() {

        UserWithRolesFilterRequest request = new UserWithRolesFilterRequest();

        request.setPage(0);
        request.setSize(5);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(BASE_URI + "/filter")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(3));
    }

    @Test
    @DisplayName("Filter users of organization when user and roles specified expected success")
    public void filterUsersOfOrganizationWhenUserAndRolesExpectedSuccess() {

        UserWithRolesFilterRequest request = new UserWithRolesFilterRequest();

        request.setPage(0);
        request.setSize(5);

        UserAndRoles userAndRoles = new UserAndRoles();

        userAndRoles.setUserId(100L);
        userAndRoles.setRolesIds(Collections.singletonList(100L));

        request.setUserAndRoles(Collections.singletonList(userAndRoles));

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(BASE_URI + "/filter")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(1))
                .body("content[0].roles", hasSize(1));
    }

    @Test
    @DisplayName("Filter users of organization with complex query expected success")
    public void filterUsersWithComplexQueryOfOrganizationExpectedSuccess() {

        final Long userId = 102L;

        UserWithRolesFilterRequest request = new UserWithRolesFilterRequest();

        request.setPage(0);
        request.setSize(5);
        request.setEmail("email");
        request.setLogin("_Us");
        request.setFirstName("ax");
        request.setLastName("Pay");
        request.setCreatedDate(new DateRange(
                Instant.now().minus(1, ChronoUnit.DAYS),
                Instant.now().plus(2, ChronoUnit.DAYS)
        ));
        request.setSortBy("login");
        request.setSortDirection(SortDirections.DESC);


        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(BASE_URI + "/filter")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(1))
                .body("content[0].id", is(userId.intValue()));
    }

    @Test
    @DisplayName("Get user by id expected success response")
    public void getUserByIdExpectedSuccess() {

        final int userId = 101;

        given()
                .contentType(ContentType.JSON)
                .header(USER_ID_HEADER_NAME, userId)
                .when()
                .get(BASE_URI + "/" + userId)
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
    @DisplayName("Get user by id expected success response")
    public void getUserByIdExpectedNotFound() {

        final int userId = 10000;

        given()
                .contentType(ContentType.JSON)
                .header(USER_ID_HEADER_NAME, userId)
                .when()
                .get(BASE_URI + "/{userId}", userId)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", is("User is not found"));
    }

}