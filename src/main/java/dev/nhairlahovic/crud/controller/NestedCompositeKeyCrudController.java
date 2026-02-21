package dev.nhairlahovic.crud.controller;

import dev.nhairlahovic.crud.mapper.CompositeKeyResourceMapper;
import dev.nhairlahovic.crud.model.BaseCompositeKeyEntity;
import dev.nhairlahovic.crud.service.NestedCrudService;
import dev.nhairlahovic.crud.validator.ValidationGroups;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * This abstract class provides a generic CRUD (Create, Read, Update, Delete) controller
 * for entities that are related to a parent entity, including support for composite keys.
 * It is designed to work with various types of entities and DTOs (Data Transfer Objects).
 * It must be extended by concrete classes that specify the types of the parent entity, related entity,
 * request DTO, response DTO, the ID types for both the parent and related entities, and the type used
 * for the composite key in the URL.
 *
 * @param <P>  The type of the parent entity that this controller will manage.
 * @param <E>  The type of the related entity that this controller will manage.
 * @param <R>  The type of the request DTO.
 * @param <D>  The type of the response DTO.
 * @param <PI> The type of the ID of the parent entity.
 * @param <I>  The type of the ID of the related entity.
 * @param <C>  The type used for the composite key in the URL.
 */
@RequiredArgsConstructor
public abstract class NestedCompositeKeyCrudController<P, E extends BaseCompositeKeyEntity<I>, R, D, PI, I, C> {

    protected final NestedCrudService<P, E, PI, I> nestedCrudService;
    protected final CompositeKeyResourceMapper<E, R, D, PI, I> mapper;

    @GetMapping
    public List<D> getAllResources(@PathVariable PI parentId) {
        return nestedCrudService.getAllByParent(parentId)
                .stream()
                .map(mapper::mapToDto)
                .toList();
    }

    @GetMapping("/{id}")
    public D getResourceById(@PathVariable PI parentId, @PathVariable C id) {
        I compositeId = convertToCompositeId(parentId, id);
        E entity = nestedCrudService.getById(parentId, compositeId);
        return mapper.mapToDto(entity);
    }

    @PostMapping
    public D createResource(@PathVariable PI parentId, @Validated(ValidationGroups.All.class) @RequestBody R request) {
        E entity = mapper.mapToEntity(parentId, request);
        E savedEntity = nestedCrudService.create(entity);
        return mapper.mapToDto(savedEntity);
    }

    @PutMapping("/{id}")
    public D updateResource(@PathVariable PI parentId, @PathVariable C id, @Validated(ValidationGroups.All.class) @RequestBody R request) {
        I compositeId = convertToCompositeId(parentId, id);
        E updatedEntity = mapper.updateEntity(parentId, request);
        E savedEntity = nestedCrudService.update(compositeId, updatedEntity);
        return mapper.mapToDto(savedEntity);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteResource(@PathVariable PI parentId, @PathVariable C id) {
        I compositeId = convertToCompositeId(parentId, id);
        nestedCrudService.delete(parentId, compositeId);
    }

    /**
     * Converts the URL parameter of type C, in combination with the parent ID of type PI,
     * to the composite key type I.
     *
     * @param parentId The ID of the parent entity, which may be needed to construct the composite key.
     * @param id       The URL parameter representing part of the composite key.
     * @return The composite key of type I.
     */
    protected abstract I convertToCompositeId(PI parentId, C id);
}
