package dev.nhairlahovic.crud.filter;

import jakarta.persistence.criteria.*;
import lombok.NonNull;
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
    public Predicate toPredicate(@NonNull Root<T> root, CriteriaQuery<?> query, @NonNull CriteriaBuilder builder) {
        FilterableFields filterFields = criteria.getFilterFields();
        var keys = filterFields.getKeys();
        var strategy = filterFields.getFilterMatchingStrategy();
        var operation = criteria.getOperation();

        return switch (strategy) {
            case SINGLE -> {
                String key = keys.get(0);
                yield buildPredicate(root, builder, key, operation, filterValue);
            }

            case CONCAT -> {
                // If no keys are provided, return a neutral (always-true) predicate
                if (keys.isEmpty()) {
                    yield builder.conjunction();
                }

                // Start with first key
                Expression<String> concatenated = builder.coalesce(root.get(keys.get(0)).as(String.class), "");

                for (int i = 1; i < keys.size(); i++) {
                    // Coalesce fields to empty string if null
                    Expression<String> fieldExpr = builder.coalesce(root.get(keys.get(i)).as(String.class), "");
                    // Add a space before concatenating the next field
                    concatenated = builder.concat(concatenated, builder.literal(" "));
                    concatenated = builder.concat(concatenated, fieldExpr);
                }

                yield buildPredicateOnExpression(builder, concatenated, operation, filterValue);
            }

            case OR -> {
                // Apply OR across predicates for each key
                Predicate[] predicates = keys.stream()
                        .map(key -> buildPredicate(root, builder, key, operation, filterValue))
                        .toArray(Predicate[]::new);
                yield builder.or(predicates);
            }

            case AND -> {
                // Apply AND across predicates for each key
                Predicate[] predicates = keys.stream()
                        .map(key -> buildPredicate(root, builder, key, operation, filterValue))
                        .toArray(Predicate[]::new);
                yield builder.and(predicates);
            }
        };
    }

    private Predicate buildPredicate(Root<T> root, CriteriaBuilder builder,
                                     String key, FilterOperation operation, String value) {
        return switch (operation) {
            case EQUALITY -> builder.equal(root.get(key), value);
            case LIKE -> builder.like(
                    builder.lower(root.get(key)),
                    "%" + value.toLowerCase() + "%"
            );
        };
    }

    private Predicate buildPredicateOnExpression(CriteriaBuilder builder, Expression<String> expr,
                                                 FilterOperation operation, String value) {
        return switch (operation) {
            case EQUALITY -> builder.equal(expr, value);
            case LIKE -> builder.like(
                    builder.lower(expr),
                    "%" + value.toLowerCase() + "%"
            );
        };
    }
}
