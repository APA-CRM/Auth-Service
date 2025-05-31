package com.crm.auth.filter;

import com.crm.auth.filter.criteria.role.RolePredicateBuilder;
import com.crm.auth.persistance.entity.Role;
import com.crm.sharedlib.filter.criteria.BaseCriteriaFilter;
import com.crm.sharedlib.filter.criteria.impl.DefaultCountableFilter;
import com.crm.sharedlib.filter.criteria.impl.DefaultOrderByApplier;
import com.crm.sharedlib.filter.criteria.impl.DefaultPageableFilter;
import com.crm.sharedlib.filter.criteria.interfaces.CountableFilter;
import com.crm.sharedlib.filter.criteria.interfaces.OrderByApplier;
import com.crm.sharedlib.filter.criteria.interfaces.PageableBuilder;
import com.crm.sharedlib.filter.criteria.interfaces.PredicateBuilder;
import org.springframework.stereotype.Service;

@Service
public class RoleFilter extends BaseCriteriaFilter<Role> {

    @Override
    protected Class<Role> getEntityClass() {
        return Role.class;
    }

    @Override
    protected String getDefaultSortField() {
        return "id";
    }

    @Override
    protected PredicateBuilder<Role> getPredicateBuilder() {
        return new RolePredicateBuilder();
    }

    @Override
    protected CountableFilter<Role> getCountableFilter() {
        return new DefaultCountableFilter<>();
    }

    @Override
    protected PageableBuilder getPageableBuilder() {
        return new DefaultPageableFilter();
    }

    @Override
    protected OrderByApplier<Role> getOrderByApplier() {
        return new DefaultOrderByApplier<>();
    }

}
