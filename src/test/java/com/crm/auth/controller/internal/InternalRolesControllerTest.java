package com.crm.auth.controller.internal;


import com.crm.auth.BaseIntegrationTest;
import com.crm.sharedlib.dto.request.CreateRoleRequest;
import com.crm.sharedlib.dto.request.ResourceWithActionsRequest;
import com.crm.sharedlib.dto.request.RoleFilterRequest;
import com.crm.sharedlib.dto.request.RoleRequest;
import com.crm.sharedlib.enums.Action;
import com.crm.sharedlib.enums.Resource;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Sql(scripts = "classpath:sql/insertTestRoles.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:sql/deleteTestRoles.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class InternalRolesControllerTest extends BaseIntegrationTest {

    private final String BASE_URI = "/api/internal/roles";

    @Test
    @DisplayName("Create role expected success response")
    public void createRoleExpectedSuccess() {

        ResourceWithActionsRequest resource1 = new ResourceWithActionsRequest();
        resource1.setResource(Resource.USERS);
        resource1.setActions(List.of(Action.CREATE, Action.DELETE, Action.UPDATE, Action.READ));

        ResourceWithActionsRequest resource2 = new ResourceWithActionsRequest();

        resource2.setResource(Resource.ORGANIZATIONS);
        resource2.setActions(Collections.singletonList(Action.ALL));

        CreateRoleRequest request = new CreateRoleRequest();

        request.setName("Admin");
        request.setIsDeletable(false);
        request.setResources(List.of(resource1, resource2));

        given()
                .contentType(ContentType.JSON)
                .when()
                .body(request)
                .post(BASE_URI)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", notNullValue())
                .body("name", is("Admin"))
                .body("accessControls", hasItems(
                        hasEntry("resource", Resource.USERS.getName()),
                        hasEntry("resource", Resource.ORGANIZATIONS.getName())
                ))
                .body("createdAt", notNullValue())
                .body("updateAt", notNullValue());

    }

    @Test
    @DisplayName("Create role when only ALL resource used expected success response")
    public void createRoleWhenAllResourceUsedExpectedSuccess() {

        ResourceWithActionsRequest resource = new ResourceWithActionsRequest();
        resource.setResource(Resource.ALL);
        resource.setActions(List.of(Action.ALL));

        CreateRoleRequest request = new CreateRoleRequest();

        request.setName("Admin");
        request.setIsDeletable(false);
        request.setResources(Collections.singletonList(resource));

        given()
                .contentType(ContentType.JSON)
                .when()
                .body(request)
                .post(BASE_URI)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", notNullValue())
                .body("name", is("Admin"))
                .body("accessControls", hasItem(
                        hasEntry("resource", Resource.ALL.getName())
                ))
                .body("createdAt", notNullValue())
                .body("updateAt", notNullValue());

    }

    @Test
    @DisplayName("Create role when ALL resource used with others resources expected bad response response")
    public void createRoleWhenAllResourceWithOthersResourcesSpecifiedExpectedBadRequest() {

        ResourceWithActionsRequest resource1 = new ResourceWithActionsRequest();
        resource1.setResource(Resource.USERS);
        resource1.setActions(List.of(Action.CREATE, Action.DELETE, Action.UPDATE, Action.READ));

        ResourceWithActionsRequest resource2 = new ResourceWithActionsRequest();
        resource2.setResource(Resource.ORGANIZATIONS);
        resource2.setActions(Collections.singletonList(Action.ALL));

        ResourceWithActionsRequest resource3 = new ResourceWithActionsRequest();
        resource3.setResource(Resource.ALL);
        resource3.setActions(Collections.singletonList(Action.ALL));

        CreateRoleRequest request = new CreateRoleRequest();

        request.setName("Admin");
        request.setIsDeletable(false);
        request.setResources(List.of(resource1, resource2, resource3));

        given()
                .contentType(ContentType.JSON)
                .when()
                .body(request)
                .post(BASE_URI)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", is("You can not use the ALL resource with other resources"));
    }

    @Test
    @DisplayName("Get role by id expected success")
    public void getRoleByIdExpectedSuccess() {
        final int roleId = 100;

        given()
                .contentType(ContentType.JSON)
                .when()
                .param("roleId", roleId)
                .get(BASE_URI)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("[0].id", is(roleId))
                .body("[0].name", is("Admin"))
                .body("[0].accessControls[0].resource", is(Resource.ALL.getName()))
                .body("[0].accessControls[0].actions", containsInAnyOrder(Resource.ALL.getName()));
    }

    @Test
    @DisplayName("Filter roles when name is specified expected success response")
    public void filterRoleWhenNameIsSpecifiedExpectedSuccess() {

        RoleFilterRequest request = new RoleFilterRequest();

        request.setPage(0);
        request.setSize(5);
        request.setName("Ad");

        given()
                .contentType(ContentType.JSON)
                .when()
                .body(request)
                .post(BASE_URI + "/filter")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(1))
                .body("page", notNullValue());

    }

    @Test
    @DisplayName("Filter roles when name is specified expected success response")
    public void filterRoleWhenRolesIdIsSpecifiedExpectedSuccess() {

        RoleFilterRequest request = new RoleFilterRequest();

        request.setPage(0);
        request.setSize(5);
        request.setRolesId(Collections.singletonList(200L));

        given()
                .contentType(ContentType.JSON)
                .when()
                .body(request)
                .post(BASE_URI + "/filter")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(1))
                .body("content[0].id", is(request.getRolesId().getFirst().intValue()))
                .body("page", notNullValue());

    }

    @Test
    @DisplayName("Update role expected success response")
    public void updateRoleExpectedSuccess() {

        final int roleId = 200;

        ResourceWithActionsRequest resource1 = new ResourceWithActionsRequest();
        resource1.setResource(Resource.USERS);
        resource1.setActions(List.of(Action.CREATE, Action.DELETE, Action.UPDATE, Action.READ));

        ResourceWithActionsRequest resource2 = new ResourceWithActionsRequest();
        resource2.setResource(Resource.ORGANIZATIONS);
        resource2.setActions(Collections.singletonList(Action.ALL));

        RoleRequest request = new RoleRequest();

        request.setName("Programmer");
        request.setResources(List.of(resource1, resource2));

        given()
                .contentType(ContentType.JSON)
                .when()
                .body(request)
                .put(BASE_URI + "/{roleId}", roleId)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", is(roleId))
                .body("name", is(request.getName()))
                .body("accessControls", hasItems(
                        hasEntry("resource", resource1.getResource().getName()),
                        hasEntry("resource", resource2.getResource().getName())
                ));

    }

    @Test
    @DisplayName("Delete role expected success response")
    public void deleteRoleExpectedSuccess() {
        final int roleId = 200;

        given()
                .contentType(ContentType.JSON)
                .when()
                .delete(BASE_URI + "/{roleId}", roleId)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("Delete role when role is undeletable expected forbidden response")
    public void deleteRoleWhenRoleIsUndeletableExpectedForbidden() {
        final int roleId = 100;

        given()
                .contentType(ContentType.JSON)
                .when()
                .delete(BASE_URI + "/{roleId}", roleId)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body("message", is("This role can't be deleted"));
    }

}