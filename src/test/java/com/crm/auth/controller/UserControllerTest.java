package com.crm.auth.controller;

import com.crm.auth.BaseIntegrationTest;
import com.crm.auth.dto.request.UserUpdateRequest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
                .body("phoneNumber", notNullValue())
                .body("aboutYourself", notNullValue())
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
                .body("[0].phoneNumber", notNullValue())
                .body("[0].aboutYourself", notNullValue())
                .body("[0].fullName", is("Serious Sam"))
                .body("[0].firstName", is("Serious"))
                .body("[0].lastName", is("Sam"));
    }

    @Test
    @DisplayName("Get user by ID expected success response")
    public void getUserByIdExpectedSuccess() {

        final int userId = 101;

        given()
                .contentType(ContentType.JSON)
                .header(USER_ID_HEADER_NAME, userId)
                .when()
                .get(BASE_URI + "/{userId}", userId)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(userId))
                .body("login", notNullValue())
                .body("email", notNullValue())
                .body("phoneNumber", notNullValue())
                .body("aboutYourself", notNullValue())
                .body("fullName", is("Serious Sam"))
                .body("firstName", is("Serious"))
                .body("lastName", is("Sam"));
    }

    @Test
    @DisplayName("Update user by ID expected success response")
    public void updateUserByIdExpectedSuccess() {

        final int userId = 101;

        UserUpdateRequest request = new UserUpdateRequest();
        request.setFirstName("Artem");
        request.setLastName("Syrnik");
        request.setPhoneNumber("+380501234569");
        request.setAboutYourself("I love Frontend, maybe...");
        given()
                .contentType(ContentType.JSON)
                .header(USER_ID_HEADER_NAME, userId)
                .when()
                .body(request)
                .patch(BASE_URI + "/{userId}", userId)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(userId))
                .body("login", notNullValue())
                .body("email", notNullValue())
                .body("phoneNumber", is(request.getPhoneNumber()))
                .body("aboutYourself", is(request.getAboutYourself()))
                .body("fullName", is("Artem Syrnik"))
                .body("firstName", is(request.getFirstName()))
                .body("lastName", is(request.getLastName()));
    }

    @Test
    @DisplayName("Update user by ID when updating not own profile expected forbidden response")
    public void updateUserByIdFromAnotherIdExpectedForbidden() {

        final int userId = 101;

        UserUpdateRequest request = new UserUpdateRequest();
        request.setFirstName("Artem");
        request.setLastName("Syrnik");
        request.setPhoneNumber("+380501234569");
        request.setAboutYourself("I love Frontend, maybe...");
        given()
                .contentType(ContentType.JSON)
                .header(USER_ID_HEADER_NAME, 102)
                .when()
                .body(request)
                .patch(BASE_URI + "/{userId}", userId)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body("message",is("You can only update your own profile."));
    }

    @ParameterizedTest
    @CsvSource({
            "'+38067 123456'",        // недостаточно цифр
            "'+38 0 67 123 4567'",    // неправильный формат
            "'+123 (4567) 123-456'",  // код оператора слишком длинный
            "'+12)345( 1234-5678'",   // неправильные скобки
            "'+1-abc-def-ghij'",      // буквы вместо цифр
            "'++380671234567'",       // двойной +
            "'+38067_123_4567'",      // символ _
            "'+9999 123 456 7890'",   // слишком длинный код страны
            "'123456'",               // слишком короткий
            "'380671234567"           // нет +
    })
    @DisplayName("Update user by ID with invalid phone number expected bad request")
    public void updateUserPhoneNumberExpectedBadRequest(String phoneNumber) {

        final int userId = 101;

        UserUpdateRequest request = new UserUpdateRequest();
        request.setPhoneNumber(phoneNumber);
        given()
            .contentType(ContentType.JSON)
            .header(USER_ID_HEADER_NAME, userId)
            .body(request)
            .when()
            .patch(BASE_URI + "/{userId}", userId)
            .then()
            .log().all()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

}