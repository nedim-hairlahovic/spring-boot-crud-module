package dev.nhairlahovic.crud.filter;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class FilterSpecification<T> implements Specification<T> {

    private final FilterCriteria criteria;
    private final String filterValue;

    public FilterSpecification(FilterCriteria criteria, String filterValue) {
        super();
        this.criteria = criteria;
        this.filterValue = filterValue;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        return switch (criteria.getOperation()) {
            case EQUALITY -> builder.equal(
                    root.get(criteria.getKey()), filterValue);
            case LIKE -> builder.like(
                    builder.lower(root.get(criteria.getKey())), "%" + filterValue.toLowerCase() + "%");
        };
    }
}

