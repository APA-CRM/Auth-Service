package com.crm.auth.feign;

import com.crm.sharedlib.dto.response.OrganizationUserRolesResponse;
import com.crm.sharedlib.feign.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "main-service", configuration = FeignClientConfig.class)
public interface MainClient {

    @GetMapping("/api/internal/organizations/{organizationId}/users/{userId}")
    OrganizationUserRolesResponse getOrganizationUserRoles(
            @PathVariable("organizationId") Long organizationId,
            @PathVariable("userId") Long userId
    );

}
