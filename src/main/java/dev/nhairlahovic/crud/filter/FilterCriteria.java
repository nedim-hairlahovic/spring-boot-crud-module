package dev.nhairlahovic.crud.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterCriteria {
    private String key;
    private FilterOperation operation;
}
