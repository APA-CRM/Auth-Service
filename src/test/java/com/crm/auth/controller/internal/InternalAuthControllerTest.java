package com.crm.auth.controller.internal;

import com.crm.auth.BaseIntegrationTest;
import com.crm.auth.service.JwtService;
import com.crm.sharedlib.core.dto.request.AuthorizationRequest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static com.crm.sharedlib.core.consts.CrmHeaders.ORGANIZATION_ID_HEADER_NAME;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

class InternalAuthControllerTest extends BaseIntegrationTest {

    private final static String BASE_URI = "/api/internal/auth";

    @Autowired
    private JwtService jwtService;

    @Test
    @DisplayName("Authorize expected success")
    public void authorizeExpectedSuccess() {
        final String token = jwtService.generateToken(1L, "login");

        AuthorizationRequest request = new AuthorizationRequest(
                token, "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:122.0) Gecko/20100101 Firefox/122.0"
        );

        given()
                .contentType(ContentType.JSON)
                .when()
                .body(request)
                .post(BASE_URI + "/authorize")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(1))
                .body("login", is("login"));
    }


    @Test
    @DisplayName("Authorize when token is expired expected unauthorized")
    public void authorizeWhenTokenIsExpiredExpectedUnauthorized() {
        final String expiredtoken = "eyJhbGciOiJIUzUxMiJ9" +
                ".eyJ1c2VySWQiOjEsImxvZ2luIjoiUGF2ZWwiLCJpYXQiOjE3NDI0MDg5MTQsImV4cCI6MTc0MjQxMDcxNH0" +
                ".aR8_oJiy_S7ef6O9D_8nJqAj0wMLTW65I2dbMN-WH_5AwvaxMiNlgRGjVuTdXq5m4ybeH-DPeTKEW6YWGibCZg";

        AuthorizationRequest request = new AuthorizationRequest();

        request.setAccessToken(expiredtoken);

        given()
                .contentType(ContentType.JSON)
                .header(ORGANIZATION_ID_HEADER_NAME, 1)
                .when()
                .body(request)
                .post(BASE_URI + "/authorize")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("message", is("Unauthorized"));
    }

    @Test
    @DisplayName("Authorize when token with wrong JWT signature expected unauthorized")
    public void authorizeWhenTokenWithWrongSignatureExpectedUnauthorized() {
        final String wrongJwt = "eyJhbGciOiJIUzI1NiJ9." +
                "eyJJc3N1ZXIiOiJJc3N1ZXIiLCJpZCI6IjEiLCJsb2dpbiI6InRlc3QiLCJleHAiOjE3NTIzMzE1NDIsImlhdCI6MTc1MjMzMTU0Mn0." +
                "n_Lepi7ESJhxl3BdN8RBmI4mBzSWwiCudXDv5RL3FkM";

        AuthorizationRequest request = new AuthorizationRequest();

        request.setAccessToken(wrongJwt);

        given()
                .contentType(ContentType.JSON)
                .header(ORGANIZATION_ID_HEADER_NAME, 1)
                .when()
                .body(request)
                .post(BASE_URI + "/authorize")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("message", is("Unauthorized"));
    }


}