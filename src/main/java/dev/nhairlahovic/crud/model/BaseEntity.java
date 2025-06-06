package dev.nhairlahovic.crud.model;

public interface BaseEntity<T> {
    T getId();

    void setId(T id);
}
