package com.crm.auth.service.checker;

import com.crm.sharedlib.rbac.dto.ResourcePermission;
import com.crm.sharedlib.rbac.dto.UserPermission;
import com.crm.sharedlib.core.dto.request.AuthorizationWithUriAndHttpMethodRequest;
import com.crm.sharedlib.core.enums.Action;
import com.crm.sharedlib.core.enums.Resource;
import com.crm.sharedlib.core.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.util.*;
import java.util.stream.Collectors;

import static com.crm.sharedlib.core.enums.Action.*;
import static com.crm.sharedlib.core.enums.Resource.*;
import static org.springframework.http.HttpMethod.*;

@Service
@RequiredArgsConstructor
public class PermissionChecker {

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    private static final Map<Resource, List<String>> RESOURCE_TO_URI = Map.of(
            Resource.ALL, List.of(
                    "/api/organizations/*/users/**",
                    "/api/organizations/*/roles/**",
                    "/api/organizations/*/users/*/roles/**",
                    "/api/organizations/*/invitations/**",
                    "/api/organizations/*/files/**", "/api/files/**",
                    "/api/organizations/**"
            ),
            USERS, Collections.singletonList("/api/organizations/*/users/**"),
            ROLES, Collections.singletonList("/api/organizations/*/roles/**"),
            USERS_ROLES, Collections.singletonList("/api/organizations/*/users/*/roles/**"),
            INVITATIONS, Collections.singletonList("/api/organizations/*/invitations/**"),
            FILES, List.of("/api/organizations/*/files/**", "/api/files/**"),
            ORGANIZATIONS, Collections.singletonList("/api/organizations/**")
    );

    private static final Map<Action, List<HttpMethod>> HTTP_METHOD_ACTIONS = Map.of(
            Action.ALL, List.of(GET, POST, PUT, PATCH, HttpMethod.DELETE),
            READ, Collections.singletonList(GET),
            CREATE, Collections.singletonList(POST),
            UPDATE, List.of(PUT, PATCH),
            Action.DELETE, Collections.singletonList(HttpMethod.DELETE)
    );

    public static boolean isValidActionToHttpMethod(HttpMethod httpMethod, Collection<Action> actions) {
        for (Action action : actions) {
            List<HttpMethod> httpMethodList =
                    HTTP_METHOD_ACTIONS.getOrDefault(action, Collections.emptyList());

            if (httpMethodList.contains(httpMethod)) {
                return true;
            }

        }

        return false;
    }

    public static boolean isValidResourceToUri(String uri, List<Resource> resources) {

        for (Resource resource : resources) {
            List<String> uris =
                    RESOURCE_TO_URI.getOrDefault(resource, Collections.emptyList());

            for (String pattern : uris) {
                boolean isMatched = ANT_PATH_MATCHER.match(pattern, uri);

                if (isMatched) {
                    return true;
                }
            }

        }

        return false;
    }

    public void checkUserPermission(
            UserPermission userPermission,
            AuthorizationWithUriAndHttpMethodRequest request
    ) {

        HttpMethod httpMethod = HttpMethod.valueOf(request.getHttpMethodName());

        List<ResourcePermission> resourcePermissions = userPermission.getResourcePermissions();

        List<Resource> resources =
                resourcePermissions.stream().map(ResourcePermission::getResource).toList();

        Set<Action> actions = resourcePermissions.stream()
                .flatMap(permission -> permission.getActions().stream())
                .collect(Collectors.toSet());

        boolean isValidHttpMethod = isValidActionToHttpMethod(httpMethod, actions);

        boolean isValidUri = isValidResourceToUri(request.getUri(), resources);

        if (!(isValidHttpMethod && isValidUri)) {
            throw new ForbiddenException("You can't access this resource");
        }

    }


}
