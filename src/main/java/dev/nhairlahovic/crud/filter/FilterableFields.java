package dev.nhairlahovic.crud.filter;

import lombok.Getter;

import java.util.List;
import java.util.Objects;

@Getter
public class FilterableFields {
    private final List<String> keys;
    private final FilterMatchingStrategy filterMatchingStrategy;

    private FilterableFields(List<String> keys, FilterMatchingStrategy filterMatchingStrategy) {
        this.keys = List.copyOf(keys);
        this.filterMatchingStrategy = Objects.requireNonNull(filterMatchingStrategy);
    }

    public static FilterableFields of(List<String> keys, FilterMatchingStrategy filterMatchingStrategy) {
        return new FilterableFields(keys, filterMatchingStrategy);
    }

    public enum FilterMatchingStrategy {
        SINGLE,        // default, for simple key
        CONCAT,        // concatenate values from keys and apply filter
        OR,            // apply OR on each key
        AND            // apply AND on each key
    }
}
