package com.crm.auth.filter;

import com.crm.auth.filter.criteria.user.UserPredicateBuilder;
import com.crm.auth.persistance.entity.User;
import com.crm.sharedlib.core.filter.criteria.BaseCriteriaFilter;
import com.crm.sharedlib.core.filter.criteria.impl.DefaultCountableFilter;
import com.crm.sharedlib.core.filter.criteria.impl.DefaultOrderByApplier;
import com.crm.sharedlib.core.filter.criteria.impl.DefaultPageableFilter;
import com.crm.sharedlib.core.filter.criteria.interfaces.CountableFilter;
import com.crm.sharedlib.core.filter.criteria.interfaces.OrderByApplier;
import com.crm.sharedlib.core.filter.criteria.interfaces.PageableBuilder;
import com.crm.sharedlib.core.filter.criteria.interfaces.PredicateBuilder;
import org.springframework.stereotype.Service;

@Service
public class UserFilter extends BaseCriteriaFilter<User> {

    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }

    @Override
    protected String getDefaultSortField() {
        return "id";
    }

    @Override
    protected PredicateBuilder<User> getPredicateBuilder() {
        return new UserPredicateBuilder();
    }

    @Override
    protected CountableFilter<User> getCountableFilter() {
        return new DefaultCountableFilter<>();
    }

    @Override
    protected PageableBuilder getPageableBuilder() {
        return new DefaultPageableFilter();
    }

    @Override
    protected OrderByApplier<User> getOrderByApplier() {
        return new DefaultOrderByApplier<>();
    }

}
