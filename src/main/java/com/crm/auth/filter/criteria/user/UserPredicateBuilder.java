package com.crm.auth.filter.criteria.user;

import com.crm.auth.persistance.entity.User;
import com.crm.sharedlib.core.dto.request.BaseFilterRequest;
import com.crm.sharedlib.core.dto.request.UserWithRolesFilterRequest;
import com.crm.sharedlib.core.dto.response.UserAndRoles;
import com.crm.sharedlib.core.filter.criteria.interfaces.PredicateBuilder;
import com.crm.sharedlib.core.utils.PredicateFilterUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

public class UserPredicateBuilder implements PredicateBuilder<User> {

    @Override
    public List<Predicate> buildPredicates(
            BaseFilterRequest request,
            CriteriaBuilder builder,
            Root<User> root
    ) {
        List<Predicate> predicates = new ArrayList<>();

        if (request instanceof UserWithRolesFilterRequest userFilterRequest) {
            this.filterByLogin(userFilterRequest, predicates, builder, root);

            this.filterByEmail(userFilterRequest, predicates, builder, root);

            this.filterByFirstName(userFilterRequest, predicates, builder, root);

            this.filterByLastName(userFilterRequest, predicates, builder, root);

            this.filterByCreatedDate(userFilterRequest, predicates, builder, root);

            this.filterByUpdateDate(userFilterRequest, predicates, builder, root);

            this.filterByUsersId(userFilterRequest, predicates, builder, root);
        }

        return predicates;
    }

    private void filterByLogin(
            UserWithRolesFilterRequest request,
            List<Predicate> predicates,
            CriteriaBuilder builder,
            Root<User> root
    ) {
        PredicateFilterUtils.filterBySymbolContains(
                request.getLogin(), "login",
                predicates, builder, root
        );
    }

    private void filterByEmail(
            UserWithRolesFilterRequest request,
            List<Predicate> predicates,
            CriteriaBuilder builder,
            Root<User> root
    ) {
        PredicateFilterUtils.filterBySymbolContains(
                request.getEmail(), "email",
                predicates, builder, root
        );
    }

    private void filterByFirstName(
            UserWithRolesFilterRequest request,
            List<Predicate> predicates,
            CriteriaBuilder builder,
            Root<User> root
    ) {
        PredicateFilterUtils.filterBySymbolContains(
                request.getFirstName(), "firstName",
                predicates, builder, root
        );
    }

    private void filterByLastName(
            UserWithRolesFilterRequest request,
            List<Predicate> predicates,
            CriteriaBuilder builder,
            Root<User> root
    ) {
        PredicateFilterUtils.filterBySymbolContains(
                request.getLastName(), "lastName",
                predicates, builder, root
        );
    }

    private void filterByCreatedDate(
            UserWithRolesFilterRequest request,
            List<Predicate> predicates,
            CriteriaBuilder builder,
            Root<User> root
    ) {
        PredicateFilterUtils.filterByDateRange(
                request.getCreatedDate(), "createdAt",
                predicates, builder, root
        );
    }

    private void filterByUpdateDate(
            UserWithRolesFilterRequest request,
            List<Predicate> predicates,
            CriteriaBuilder builder,
            Root<User> root
    ) {
        PredicateFilterUtils.filterByDateRange(
                request.getUpdatedDate(), "updateAt",
                predicates, builder, root
        );
    }

    private void filterByUsersId(
            UserWithRolesFilterRequest request,
            List<Predicate> predicates,
            CriteriaBuilder builder,
            Root<User> root
    ) {
        if (isNull(request.getUserAndRoles())) {
            return;
        }

        List<Long> usersId = request.getUserAndRoles()
                .stream()
                .map(UserAndRoles::getUserId)
                .toList();

        predicates.add(
                root.get("id").in(usersId)
        );
    }

}
