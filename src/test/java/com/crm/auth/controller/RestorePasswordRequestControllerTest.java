package com.crm.auth.controller;

import com.crm.auth.BaseIntegrationTest;
import com.crm.auth.dto.request.RestorePasswordRequest;
import com.crm.auth.dto.request.VerificationCodeRequest;
import com.crm.auth.persistance.entity.PasswordRestoreRequest;
import com.crm.auth.persistance.repository.PasswordRestoreRequestRepository;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Sql(scripts = {
        "classpath:sql/insertTestUsers.sql",
        "classpath:sql/insertTestRefreshTokens.sql",
        "classpath:sql/insertTestPasswordRestoreRequests.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:sql/deleteTestPasswordRestoreRequest.sql",
        "classpath:sql/deleteTestRefreshTokens.sql",
        "classpath:sql/deleteTestUsers.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class RestorePasswordRequestControllerTest extends BaseIntegrationTest {

    private final static String BASE_URI = "/api/restore-password-request";

    @Autowired
    private PasswordRestoreRequestRepository restoreRequestRepository;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    @Test
    @DisplayName("Create password restore request expected success response")
    @Sql(scripts = {
            "classpath:sql/insertTestUsers.sql",
            "classpath:sql/insertTestRoles.sql",
            "classpath:sql/insertTestRefreshTokens.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:sql/deleteTestPasswordRestoreRequest.sql",
            "classpath:sql/deleteTestRefreshTokens.sql",
            "classpath:sql/deleteTestRoles.sql",
            "classpath:sql/deleteTestUsers.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void createPasswordRestoreRequestExceptedSuccess() {
        RestorePasswordRequest request = new RestorePasswordRequest();

        request.setEmail("test@gmail.com");

        given()
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .post(BASE_URI)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", notNullValue())
                .body("attemptsCount", is(0))
                .body("createAt", notNullValue())
                .body("updateAt", notNullValue());
    }

    @Test
    @DisplayName("Resend verification code expected success response")
    public void resendVerificationCodeExceptedSuccess() {
        UUID requestId = UUID.fromString("1988e512-72e4-4982-a554-fb890f863618");

        given()
                .contentType(ContentType.JSON)
                .when()
                .patch(BASE_URI + "/{requestId}/resend", requestId)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", notNullValue())
                .body("attemptsCount", is(0))
                .body("createAt", notNullValue())
                .body("updateAt", notNullValue());
    }

    @Test
    @DisplayName("Create password restore request when user already have it expected conflict response")
    public void createPasswordRestoreRequestWhenUserAlreadyHaveItExceptedConflict() {
        RestorePasswordRequest request = new RestorePasswordRequest();

        request.setEmail("test2@gmail.com");

        given()
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .post(BASE_URI)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.CONFLICT.value())
                .body("message", is("Password restore request already exists for this user. Try again later"));
    }

    @Test
    @DisplayName("Create password restore request when request is expired expected success response")
    public void createPasswordRestoreRequestWhenRequestIsExpiredExceptedSuccess() {
        RestorePasswordRequest request = new RestorePasswordRequest();

        request.setEmail("test@gmail.com");

        given()
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .post(BASE_URI)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", notNullValue())
                .body("attemptsCount", is(0))
                .body("createAt", notNullValue())
                .body("updateAt", notNullValue());
    }

    @Test
    @DisplayName("Restore password by verification code expected success response")
    public void restorePasswordByVerificationCodeExceptedSuccess() {
        UUID requestId = UUID.fromString("1988e512-72e4-4982-a554-fb890f863618");

        VerificationCodeRequest request = new VerificationCodeRequest();

        request.setVerificationCode("0011");

        given()
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .put(BASE_URI + "/{requestId}/restore-password", requestId)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("token", notNullValue())
                .body("refreshToken", notNullValue())
                .body("tokenType", notNullValue());

        Optional<PasswordRestoreRequest> requestOptional =
                restoreRequestRepository.findById(requestId);

        assertTrue(requestOptional.isEmpty());
    }

    @Test
    @DisplayName("Restore password by verification code when request does not exists expected not found response")
    public void restorePasswordByVerificationCodeWhenRequestDoesNotExistsExceptedNotFound() {
        UUID requestId = UUID.fromString("1988e512-72e4-4982-a554-fb890f863611");

        VerificationCodeRequest request = new VerificationCodeRequest();

        request.setVerificationCode("0011");

        given()
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .put(BASE_URI + "/{requestId}/restore-password", requestId)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", is("Password restore request is not found"));
    }

    @Test
    @DisplayName("Restore password by verification code when wrong code expected forbidden response")
    public void restorePasswordByVerificationCodeWhenWrongCodeExceptedForbidden() {
        UUID requestId = UUID.fromString("1988e512-72e4-4982-a554-fb890f863618");

        VerificationCodeRequest request = new VerificationCodeRequest();

        request.setVerificationCode("0000");

        given()
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .put(BASE_URI + "/{requestId}/restore-password", requestId)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body("message", is("Invalid verification code"));

        Optional<PasswordRestoreRequest> requestOptional =
                restoreRequestRepository.findById(requestId);

        assertTrue(requestOptional.isPresent());

        PasswordRestoreRequest restoreRequest = requestOptional.get();

        assertEquals(1, restoreRequest.getAttemptsCount());
    }

    @Test
    @DisplayName("Restore password by verification code when attempts excided expected forbidden response")
    public void restorePasswordByVerificationCodeWhenAttemptsExcidedExceptedForbidden() {
        UUID requestId = UUID.fromString("c974714f-532f-4844-8d16-731c299a1cf3");

        VerificationCodeRequest request = new VerificationCodeRequest();

        request.setVerificationCode("0000");

        given()
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .put(BASE_URI + "/{requestId}/restore-password", requestId)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body("message", is("Max attempts count excided"));
    }

}