package dev.nhairlahovic.crud.filter;

import lombok.Getter;

import java.util.List;

@Getter
public class FilterCriteria {
    private final FilterableFields filterFields;
    private final FilterOperation operation;

    public FilterCriteria(String key, FilterOperation operation) {
        this.filterFields = FilterableFields.of(List.of(key), FilterableFields.FilterMatchingStrategy.SINGLE);
        this.operation = operation;
    }

    public FilterCriteria(FilterableFields fields, FilterOperation operation) {
        this.filterFields = fields;
        this.operation = operation;
    }
}
