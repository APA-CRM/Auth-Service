package com.crm.auth.service;

import com.crm.auth.persistance.entity.AccessControl;
import com.crm.auth.persistance.entity.Role;
import com.crm.auth.persistance.repository.RoleRepository;
import com.crm.sharedlib.dto.request.RoleRequest;
import com.crm.sharedlib.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    @Transactional
    public Role createRole(String name) {
        Role role = new Role();

        role.setName(name);

        return roleRepository.save(role);
    }

    @Transactional
    public Role updateRole(Role role, RoleRequest request, List<AccessControl> accessControls) {

        role.setName(request.getName());
        role.addAccessControls(accessControls);

        return roleRepository.save(role);
    }

    public List<Role> getRolesById(Iterable<Long> ids) {
        return roleRepository.findAllById(ids);
    }

    public Role getRole(Long roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new NotFoundException("Role is not found"));
    }

    @Transactional
    public void deleteRole(Role role) {
        roleRepository.delete(role);
    }

}
