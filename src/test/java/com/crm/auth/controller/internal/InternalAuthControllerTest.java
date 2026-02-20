package com.crm.auth.controller.internal;

import com.crm.auth.BaseIntegrationTest;
import com.crm.auth.feign.MainClient;
import com.crm.auth.persistance.entity.redis.ResourcePermission;
import com.crm.auth.persistance.entity.redis.UserPermission;
import com.crm.auth.service.JwtService;
import com.crm.auth.service.UserPermissionService;
import com.crm.sharedlib.core.dto.request.AuthorizationRequest;
import com.crm.sharedlib.core.dto.response.OrganizationUserRolesResponse;
import com.crm.sharedlib.core.enums.Action;
import com.crm.sharedlib.core.enums.Resource;
import com.crm.sharedlib.core.exception.NotFoundException;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.crm.sharedlib.core.consts.CrmHeaders.ORGANIZATION_ID_HEADER_NAME;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

class InternalAuthControllerTest extends BaseIntegrationTest {

    private final static String BASE_URI = "/api/internal/auth";

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    @MockitoBean
    private UserPermissionService userPermissionService;

    @MockitoBean
    private MainClient mainClient;

    @Test
    @DisplayName("Authorize and check access expected success")
    public void authorizeAndCheckAccessExpectedSuccess() {
        final String token = jwtService.generateToken(1L, "login");

        AuthorizationRequest request = new AuthorizationRequest();

        request.setUri("/api/organizations/");
        request.setHttpMethodName("POST");

        ResourcePermission resourcePermission1 = new ResourcePermission();

        resourcePermission1.setResource(Resource.ORGANIZATIONS);
        resourcePermission1.setActions(List.of(Action.READ, Action.CREATE));

        ResourcePermission resourcePermission2 = new ResourcePermission();

        resourcePermission2.setResource(Resource.USERS);
        resourcePermission2.setActions(List.of(Action.READ, Action.CREATE, Action.UPDATE, Action.DELETE));

        UserPermission userPermission = new UserPermission(List.of(resourcePermission1, resourcePermission2));

        Mockito.when(userPermissionService.getUserPermission(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.of(userPermission));

        given()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(ORGANIZATION_ID_HEADER_NAME, 1)
                .when()
                .body(request)
                .post(BASE_URI + "/check-access")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(1))
                .body("login", is("login"));
    }

    @Test
    @DisplayName("Authorize expected success")
    public void authorizeExpectedSuccess() {
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
    @DisplayName("Authorize and check access expected success")
    @Sql(scripts = {
            "classpath:sql/insertTestUsers.sql",
            "classpath:sql/insertTestRoles.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:sql/deleteTestRoles.sql",
            "classpath:sql/deleteTestUsers.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void authorizeAndCheckAccessWhenUserPermissionIsNotInCacheExpectedSuccess() {
        final String token = jwtService.generateToken(1L, "login");

        AuthorizationRequest request = new AuthorizationRequest();

        request.setUri("/api/organizations/1/users/1");
        request.setHttpMethodName("POST");

        Mockito.when(userPermissionService.getUserPermission(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.empty());

        OrganizationUserRolesResponse response = OrganizationUserRolesResponse.builder()
                .userId(1L)
                .organizationId(1L)
                .rolesId(Collections.singletonList(200L))
                .build();


        Mockito.when(mainClient.getOrganizationUserRoles(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(response);

        Mockito.when(userPermissionService.saveUserPermission(
                Mockito.anyLong(), Mockito.anyLong(), Mockito.anyList())
        ).thenAnswer(invocation -> {
            List<ResourcePermission> argument = invocation.getArgument(2);

            return new UserPermission(argument);
        });


        given()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(ORGANIZATION_ID_HEADER_NAME, 1)
                .when()
                .body(request)
                .post(BASE_URI + "/check-access")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(1))
                .body("login", is("login"));

        Mockito.verify(mainClient, Mockito.atLeastOnce())
                .getOrganizationUserRoles(Mockito.anyLong(), Mockito.anyLong());

        Mockito.verify(userPermissionService, Mockito.atLeastOnce()
        ).saveUserPermission(
                Mockito.anyLong(), Mockito.anyLong(), Mockito.anyList()
        );
    }

    @Test
    @DisplayName("Authorize and check access when doesn't have permission to resource expected forbidden")
    public void authorizeAndCheckAccessWhenDoesNotHavePermissionExpectedForbidden() {
        final String token = jwtService.generateToken(1L, "login");

        AuthorizationRequest request = new AuthorizationRequest();

        request.setUri("/api/organizations/");
        request.setHttpMethodName("POST");

        ResourcePermission resourcePermission = new ResourcePermission();

        resourcePermission.setResource(Resource.USERS);
        resourcePermission.setActions(List.of(Action.READ, Action.CREATE, Action.UPDATE, Action.DELETE));

        UserPermission userPermission = new UserPermission(Collections.singletonList(resourcePermission));

        Mockito.when(userPermissionService.getUserPermission(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.of(userPermission));

        given()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(ORGANIZATION_ID_HEADER_NAME, 1)
                .when()
                .body(request)
                .post(BASE_URI + "/check-access")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body("message", is("You can't access this resource"));
    }

    @Test
    @DisplayName("Authorize and check access when doesn't have permission to resource expected forbidden")
    public void authorizeAndCheckAccessWhenAccessControlsNotFoundExpectedForbidden() {
        final String token = jwtService.generateToken(1L, "login");

        AuthorizationRequest request = new AuthorizationRequest();

        request.setUri("/api/organizations/1");
        request.setHttpMethodName("PUT");

        Mockito.when(userPermissionService.getUserPermission(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.empty());

        Mockito.when(mainClient.getOrganizationUserRoles(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new NotFoundException("User is not found"));

        given()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .when()
                .body(request)
                .post(BASE_URI + "/check-access")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body("message", is("Access controls not found"));
    }

    @Test
    @DisplayName("Authorize and check access when authorization header is not specified expected unauthorized")
    public void authorizeAndCheckAccessWhenAuthorizationHeaderIsNotSpecifiedExpectedBadRequest() {

        AuthorizationRequest request = new AuthorizationRequest();

        request.setUri("/api/organizations/");
        request.setHttpMethodName("POST");

        given()
                .contentType(ContentType.JSON)
                .header(ORGANIZATION_ID_HEADER_NAME, "1")
                .when()
                .body(request)
                .post(BASE_URI + "/check-access")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("message", is("Unauthorized"));
    }

    @Test
    @DisplayName("Authorize and check access when authorization header is not specified expected unauthorized")
    public void authorizeAndCheckAccessWhenOrganizationIdHeaderIsNotSpecifiedExpectedBadRequest() {

        AuthorizationRequest request = new AuthorizationRequest();

        request.setUri("/api/organizations/");
        request.setHttpMethodName("POST");

        given()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer null")
                .when()
                .body(request)
                .post(BASE_URI + "/check-access")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("message", is("Unauthorized"));
    }

    @Test
    @DisplayName("Authorize and check access when token is expired expected unauthorized")
    public void authorizeAndCheckAccessWhenTokenIsExpiredExpectedUnauthorized() {
        final String expiredtoken = "eyJhbGciOiJIUzUxMiJ9" +
                ".eyJ1c2VySWQiOjEsImxvZ2luIjoiUGF2ZWwiLCJpYXQiOjE3NDI0MDg5MTQsImV4cCI6MTc0MjQxMDcxNH0.aR8_oJiy_S" +
                "7ef6O9D_8nJqAj0wMLTW65I2dbMN-WH_5AwvaxMiNlgRGjVuTdXq5m4ybeH-DPeTKEW6YWGibCZg";

        AuthorizationRequest request = new AuthorizationRequest();

        request.setUri("/api/organizations/");
        request.setHttpMethodName("POST");

        given()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + expiredtoken)
                .header(ORGANIZATION_ID_HEADER_NAME, 1)
                .when()
                .body(request)
                .post(BASE_URI + "/check-access")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("message", is("Unauthorized"));
    }

    @Test
    @DisplayName("Authorize and check access when token with wrong JWT signature expected unauthorized")
    public void authorizeAndCheckAccessWhenTokenWithWrongSignatureExpectedUnauthorized() {
        final String wrongJwt = "eyJhbGciOiJIUzI1NiJ9." +
                "eyJJc3N1ZXIiOiJJc3N1ZXIiLCJpZCI6IjEiLCJsb2dpbiI6InRlc3QiLCJleHAiOjE3NTIzMzE1NDIsImlhdCI6MTc1MjMzMTU0Mn0." +
                "n_Lepi7ESJhxl3BdN8RBmI4mBzSWwiCudXDv5RL3FkM";

        AuthorizationRequest request = new AuthorizationRequest();

        request.setUri("/api/organizations/");
        request.setHttpMethodName("POST");

        given()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + wrongJwt)
                .header(ORGANIZATION_ID_HEADER_NAME, 1)
                .when()
                .body(request)
                .post(BASE_URI + "/check-access")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body("message", is("Unauthorized"));
    }


}