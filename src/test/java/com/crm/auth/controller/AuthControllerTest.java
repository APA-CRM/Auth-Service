package com.crm.auth.controller;

import com.crm.auth.BaseIntegrationTest;
import com.crm.auth.dto.request.RefreshJwtTokenRequest;
import com.crm.auth.dto.request.SignInRequest;
import com.crm.auth.dto.request.SignUpRequest;
import com.crm.auth.service.JwtService;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@Sql(scripts = {
        "classpath:sql/insertTestUsers.sql",
        "classpath:sql/insertTestRefreshTokens.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:sql/deleteTestRefreshTokens.sql",
        "classpath:sql/deleteTestUsers.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class AuthControllerTest extends BaseIntegrationTest {

    private final static String BASE_URI = "/api/auth";

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

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
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body("message", is("Wrong login or password"));
    }

    @Test
    @DisplayName("Refresh JWT token with Auth API expected success")
    public void refreshJwtTokenExpectedSuccess() {
        RefreshJwtTokenRequest request = new RefreshJwtTokenRequest();

        request.setRefreshToken("0L+INmmmYyJDlYkJSr9qGdF+AMY/ye/vJYuxo+uJu1mt4I3fY18OkFwjoMplvT/f+zU");

        given()
                .body(request)
                .contentType(ContentType.JSON)
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
    @DisplayName("Refresh JWT token with Auth API when token is not valid expected unauthorized")
    public void refreshJwtTokenWhenTokenNotValidExpectedUnauthorized() {
        RefreshJwtTokenRequest request = new RefreshJwtTokenRequest();

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
                .body("message", is("Refresh token is not valid"));
    }

    @Test
    @DisplayName("Refresh JWT token with Auth API when token is expired expected unauthorized")
    public void refreshJwtTokenWhenTokenExpiredExpectedUnauthorized() {
        RefreshJwtTokenRequest request = new RefreshJwtTokenRequest();

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
                .body("message", is("Refresh token is expired"));
    }

    @Test
    @DisplayName("Get claims expected success")
    public void getClaimsExpectedSuccess() {
        final String token = jwtService.generateToken(1L, "login");

        given()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .when()
                .get(BASE_URI + "/authorize")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(1))
                .body("login", is("login"));
    }

    @Test
    @DisplayName("Get claims when authorization header is not specified expected bad request")
    public void getClaimsWhenAuthorizationHeaderIsNotSpecifiedExpectedBadRequest() {

        given()
                .contentType(ContentType.JSON)
                .when()
                .get(BASE_URI + "/authorize")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("message", is("Authorization header is empty"));
    }

    @Test
    @DisplayName("Get claims when token is expired expected unauthorized")
    public void getClaimsWhenTokenIsExpiredExpectedUnauthorized() {
        final String expiredtoken = "eyJhbGciOiJIUzUxMiJ9" +
                ".eyJ1c2VySWQiOjEsImxvZ2luIjoiUGF2ZWwiLCJpYXQiOjE3NDI0MDg5MTQsImV4cCI6MTc0MjQxMDcxNH0.aR8_oJiy_S" +
                "7ef6O9D_8nJqAj0wMLTW65I2dbMN-WH_5AwvaxMiNlgRGjVuTdXq5m4ybeH-DPeTKEW6YWGibCZg";

        given()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + expiredtoken)
                .when()
                .get(BASE_URI + "/authorize")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("message", is("Jwt token is expired"));
    }

    @Test
    @DisplayName("Sign up request expected success")
    public void signUpExpectedSuccess() {
        SignUpRequest request = new SignUpRequest();

        request.setEmail("test@gmail.com");
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

}
