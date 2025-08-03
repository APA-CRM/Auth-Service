package com.crm.auth.utils;

import com.crm.sharedlib.enums.Resource;
import lombok.experimental.UtilityClass;
import org.springframework.util.AntPathMatcher;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.crm.sharedlib.enums.Resource.*;

@UtilityClass
public class ResourceToUriValidator {

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    private static final Map<Resource, List<String>> RESOURCE_TO_URI = Map.of(
            ALL, List.of(
                    "/api/organizations/*/users/**",
                    "/api/organizations/*/roles/**",
                    "/api/organizations/*/users/*/roles/**",
                    "/api/organizations/*/invitations/**",
                    "/api/organizations/**"
            ),
            USERS, Collections.singletonList("/api/organizations/*/users/**"),
            ROLES, Collections.singletonList("/api/organizations/*/roles/**"),
            USERS_ROLES, Collections.singletonList("/api/organizations/*/users/*/roles/**"),
            INVITATIONS, Collections.singletonList("/api/organizations/*/invitations/**"),
            FILES, List.of("/api/organizations/*/files/**", "/api/files/**"),
            ORGANIZATIONS, Collections.singletonList("/api/organizations/**")
    );

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

}
