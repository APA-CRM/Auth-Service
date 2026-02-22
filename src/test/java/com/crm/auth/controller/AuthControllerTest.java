package com.crm.auth.controller;

import com.crm.auth.BaseIntegrationTest;
import com.crm.auth.dto.request.RefreshTokenRequest;
import com.crm.auth.dto.request.SignInRequest;
import com.crm.auth.dto.request.SignUpRequest;
import com.crm.auth.feign.MainClient;
import com.crm.auth.persistance.entity.RefreshToken;
import com.crm.auth.persistance.repository.RefreshTokenRepository;
import com.crm.auth.service.UserPermissionService;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Sql(scripts = {
        "classpath:sql/insertTestUsers.sql",
        "classpath:sql/insertTestRoles.sql",
        "classpath:sql/insertTestRefreshTokens.sql",
        "classpath:sql/insertTestPasswordRestoreRequests.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:sql/deleteTestPasswordRestoreRequest.sql",
        "classpath:sql/deleteTestRefreshTokens.sql",
        "classpath:sql/deleteTestRoles.sql",
        "classpath:sql/deleteTestUsers.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class AuthControllerTest extends BaseIntegrationTest {

    private final static String BASE_URI = "/api/auth";

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;
    @MockitoBean
    private UserPermissionService userPermissionService;
    @MockitoBean
    private MainClient mainClient;

    @Test
    @DisplayName("Sign in with Auth API expected success")
    public void signInExpectedSuccess() {

        SignInRequest request = new SignInRequest();

        request.setLogin("LoginUser");
        request.setPassword("TepydIV^nG&&N4V");

        given()
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .post(BASE_URI + "/sign-in")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("token", notNullValue())
                .body("refreshToken", notNullValue())
                .body("tokenType", notNullValue());
    }

    @Test
    @DisplayName("Sign in with Auth API when wrong payload expected forbidden")
    public void signInWhenWrongPayloadExpectedForbidden() {

        SignInRequest request = new SignInRequest();

        request.setLogin("NotExists");
        request.setPassword("TepydIV^nG&&N4V");

        given()
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .post(BASE_URI + "/sign-in")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("message", is("Wrong login or password"));
    }

    @Test
    @DisplayName("Refresh JWT token with Auth API expected success")
    public void refreshJwtTokenExpectedSuccess() {
        RefreshTokenRequest request = new RefreshTokenRequest();

        request.setRefreshToken("0L+INmmmYyJDlYkJSr9qGdF+AMY/ye/vJYuxo+uJu1mt4I3fY18OkFwjoMplvT/f+zU");

        given()
                .body(request)
                .contentType(ContentType.JSON)
                .header(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:122.0) Gecko/20100101 Firefox/122.0")
                .when()
                .post(BASE_URI + "/refresh")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("token", notNullValue())
                .body("refreshToken", notNullValue())
                .body("tokenType", notNullValue());
    }

    @Test
    @DisplayName("Refresh JWT token with Auth API when device is differ expected unauthorized")
    public void refreshJwtTokenWhenDeviceInfoIsDifferExpectedUnauthorized() {
        RefreshTokenRequest request = new RefreshTokenRequest();

        request.setRefreshToken("0L+INmmmYyJDlYkJSr9qGdF+AMY/ye/vJYuxo+uJu1mt4I3fY18OkFwjoMplvT/f+zU");

        given()
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .post(BASE_URI + "/refresh")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("message", is("Unauthorized"));
    }

    @Test
    @DisplayName("Refresh JWT token with Auth API when token is not valid expected unauthorized")
    public void refreshJwtTokenWhenTokenNotValidExpectedUnauthorized() {
        RefreshTokenRequest request = new RefreshTokenRequest();

        request.setRefreshToken("ep9rgbe9bgo3bpgbepgegpergq[wpe]p.zxc;,qweberbgebgop23234324r23423e");

        given()
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .post(BASE_URI + "/refresh")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("message", is("Unauthorized"));
    }

    @Test
    @DisplayName("Refresh JWT token with Auth API when token is expired expected unauthorized")
    public void refreshJwtTokenWhenTokenExpiredExpectedUnauthorized() {
        RefreshTokenRequest request = new RefreshTokenRequest();

        request.setRefreshToken("QZWNxib69rfV2RbJy3PRf8hk9OovDKwqaMq39rLu4dc8xsI9m0193mYoRzmXVrYnNzc");

        given()
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .post(BASE_URI + "/refresh")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("message", is("Unauthorized"));
    }

    @Test
    @DisplayName("Sign up request expected success")
    public void signUpExpectedSuccess() {
        SignUpRequest request = new SignUpRequest();

        request.setEmail("newUser@gmail.com");
        request.setLogin("login");
        request.setFirstName("Jack");
        request.setLastName("Pork");
        request.setGeneratePassword(true);

        given()
                .contentType(ContentType.JSON)
                .when()
                .body(request)
                .post(BASE_URI + "/sign-up")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("token", notNullValue())
                .body("refreshToken", notNullValue())
                .body("tokenType", notNullValue());
    }

    @Test
    @DisplayName("Sign up request expected success")
    public void logoutExpectedSuccess() {
        RefreshTokenRequest request = new RefreshTokenRequest();

        request.setRefreshToken("0L+INmmmYyJDlYkJSr9qGdF+AMY/ye/vJYuxo+uJu1mt4I3fY18OkFwjoMplvT/f+zU");

        given()
                .contentType(ContentType.JSON)
                .when()
                .body(request)
                .post(BASE_URI + "/logout")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());

        Optional<RefreshToken> tokenOptional = refreshTokenRepository.findByToken(request.getRefreshToken());

        assertTrue(tokenOptional.isEmpty());
    }

}
