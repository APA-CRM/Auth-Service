package com.crm.auth.service.checker;

import com.crm.auth.persistance.entity.redis.ResourcePermission;
import com.crm.auth.persistance.entity.redis.UserPermission;
import com.crm.auth.utils.ActionToHttpMethodValidator;
import com.crm.auth.utils.ResourceToUriValidator;
import com.crm.sharedlib.dto.request.AuthorizationRequest;
import com.crm.sharedlib.enums.Action;
import com.crm.sharedlib.enums.Resource;
import com.crm.sharedlib.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionChecker {

    public void checkUserPermission(UserPermission userPermission, AuthorizationRequest request) {

        HttpMethod httpMethod = HttpMethod.valueOf(request.getHttpMethodName());

        List<ResourcePermission> resourcePermissions = userPermission.getResourcePermissions();

        List<Resource> resources =
                resourcePermissions.stream().map(ResourcePermission::getResource).toList();

        Set<Action> actions = resourcePermissions.stream()
                .flatMap(permission -> permission.getActions().stream())
                .collect(Collectors.toSet());

        boolean isValidHttpMethod =
                ActionToHttpMethodValidator.isValidActionToHttpMethod(httpMethod, actions);

        boolean isValidUri = ResourceToUriValidator.isValidResourceToUri(request.getUri(), resources);

        if (!(isValidHttpMethod && isValidUri)) {
            throw new UnauthorizedException("You can't access this resource");
        }

    }

}
