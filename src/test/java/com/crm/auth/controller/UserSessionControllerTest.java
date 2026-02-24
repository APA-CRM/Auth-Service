package com.crm.auth.controller;

import com.crm.auth.BaseIntegrationTest;
import com.crm.auth.persistance.entity.RefreshToken;
import com.crm.auth.persistance.repository.RefreshTokenRepository;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;
import java.util.UUID;

import static com.crm.sharedlib.core.consts.CrmHeaders.USER_ID_HEADER_NAME;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Sql(scripts = {
        "classpath:sql/insertTestUsers.sql",
        "classpath:sql/insertTestRefreshTokens.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:sql/deleteTestRefreshTokens.sql",
        "classpath:sql/deleteTestUsers.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class UserSessionControllerTest extends BaseIntegrationTest {

    private final static String BASE_URI = "/api/sessions";

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    @DisplayName("Get user sessions expected success")
    public void getUserSessionsExpectedSuccess() {

        given()
                .header(USER_ID_HEADER_NAME, 100)
                .contentType(ContentType.JSON)
                .when()
                .get(BASE_URI)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", everyItem(notNullValue()))
                .body("deviceInfo", everyItem(notNullValue()))
                .body("createdAt", everyItem(notNullValue()))
                .body("updatedAt", everyItem(notNullValue()));
    }

    @Test
    @DisplayName("End user session expected success")
    public void endUserSessionExpectedSuccess() {

        UUID id = UUID.fromString("32ee3ffe-b72a-46d1-9c1d-f087e0399f71");

        given()
                .header(USER_ID_HEADER_NAME, 100)
                .contentType(ContentType.JSON)
                .when()
                .delete(BASE_URI + "/{sessionId}", id)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());

        Optional<RefreshToken> tokenOptional = refreshTokenRepository.findById(id);

        assertTrue(tokenOptional.isEmpty());
    }

    @Test
    @DisplayName("End user session when wrong user expected forbidden")
    public void endUserSessionWhenWrongUserExpectedForbidden() {

        UUID id = UUID.fromString("32ee3ffe-b72a-46d1-9c1d-f087e0399f71");

        given()
                .header(USER_ID_HEADER_NAME, 101)
                .contentType(ContentType.JSON)
                .when()
                .delete(BASE_URI + "/{sessionId}", id)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body("message", is("You can't end this session"));
    }

}