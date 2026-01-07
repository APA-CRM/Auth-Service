package com.crm.auth.controller.internal;

import com.crm.auth.facade.internal.InternalRoleFacade;
import com.crm.sharedlib.core.dto.request.CreateRoleRequest;
import com.crm.sharedlib.core.dto.request.RoleFilterRequest;
import com.crm.sharedlib.core.dto.request.RoleRequest;
import com.crm.sharedlib.core.dto.response.RoleResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/internal/roles")
@RequiredArgsConstructor
public class InternalRolesController {

    private final InternalRoleFacade facade;

    @GetMapping
    public List<RoleResponse> getRolesById(
            @RequestParam("roleId") List<Long> rolesId
    ) {
        return facade.getRolesById(rolesId);
    }

    @PostMapping
    public RoleResponse createRole(
            @Valid
            @RequestBody
            CreateRoleRequest request
    ) {
        return facade.createRole(request);
    }

    @PostMapping("/filter")
    public PagedModel<RoleResponse> filterRoles(
            @Valid
            @RequestBody
            RoleFilterRequest request
    ) {
        return facade.filterRoles(request);
    }

    @PutMapping("/{roleId}")
    public RoleResponse updateRole(
            @Valid
            @RequestBody
            RoleRequest request,
            @PathVariable("roleId") Long roleId
    ) {
        return facade.updateRole(roleId, request);
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> deleteRole(
            @PathVariable("roleId") Long roleId
    ) {
        facade.deleteRole(roleId);

        return ResponseEntity.noContent().build();
    }

}
