package com.crm.auth.filter.criteria.role;

import com.crm.auth.persistance.entity.Role;
import com.crm.sharedlib.dto.request.BaseFilterRequest;
import com.crm.sharedlib.dto.request.RoleFilterRequest;
import com.crm.sharedlib.filter.criteria.interfaces.PredicateBuilder;
import com.crm.sharedlib.utils.PredicateFilterUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

public class RolePredicateBuilder implements PredicateBuilder<Role> {

    @Override
    public List<Predicate> buildPredicates(
            BaseFilterRequest request,
            CriteriaBuilder builder,
            Root<Role> root
    ) {

        List<Predicate> predicates = new ArrayList<>();

        if (request instanceof RoleFilterRequest roleFilterRequest) {

            filterByName(roleFilterRequest, predicates, builder, root);

            filterByIds(roleFilterRequest, predicates, builder, root);

        }

        return predicates;
    }

    private void filterByName(
            RoleFilterRequest request,
            List<Predicate> predicates,
            CriteriaBuilder builder,
            Root<Role> root
    ) {
        PredicateFilterUtils.filterBySymbolContains(
                request.getName(), "name",
                predicates, builder, root
        );
    }

    private void filterByIds(
            RoleFilterRequest request,
            List<Predicate> predicates,
            CriteriaBuilder builder,
            Root<Role> root
    ) {
        if (isNull(request.getRolesId())) {
            return;
        }

        predicates.add(
                root.get("id").in(request.getRolesId())
        );

    }
}
