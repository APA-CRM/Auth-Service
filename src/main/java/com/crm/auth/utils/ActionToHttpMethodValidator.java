package com.crm.auth.utils;

import com.crm.sharedlib.enums.Action;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpMethod;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.crm.sharedlib.enums.Action.*;
import static org.springframework.http.HttpMethod.*;

@UtilityClass
public class ActionToHttpMethodValidator {

    private static final Map<Action, List<HttpMethod>> HTTP_METHOD_ACTIONS = Map.of(
            ALL, List.of(GET, POST, PUT, PATCH, HttpMethod.DELETE),
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

}
