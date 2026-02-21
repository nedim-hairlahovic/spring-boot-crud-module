package dev.nhairlahovic.crud.controller;

import dev.nhairlahovic.crud.exception.ConflictingResourceOperationException;
import dev.nhairlahovic.crud.exception.ResourceNotFoundException;
import dev.nhairlahovic.crud.mapper.NestedResourceMapper;
import dev.nhairlahovic.crud.model.BaseEntity;
import dev.nhairlahovic.crud.service.NestedCrudService;
import dev.nhairlahovic.crud.validator.ValidationGroups;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This abstract class provides a generic CRUD (Create, Read, Update, Delete) controller
 * for entities that are related to a parent entity.
 * It is designed to work with various types of entities and DTOs (Data Transfer Objects).
 * It must be extended by concrete classes that specify the types of the parent entity, related entity,
 * request DTO, response DTO, and the ID types for both the parent and related entities.
 *
 * @param <P> The type of the parent entity that this controller will manage.
 * @param <E> The type of the related entity that this controller will manage.
 * @param <R> The type of the request DTO.
 * @param <D> The type of the response DTO.
 * @param <PI> The type of the ID of the parent entity.
 * @param <I> The type of the ID of the related entity.
 */
@RequiredArgsConstructor
public abstract class NestedCrudController<P, E extends BaseEntity<I>, R, D, PI, I> {

    protected final NestedCrudService<P, E, PI, I> nestedCrudService;
    protected final NestedResourceMapper<E, R, D, PI, I> mapper;

    @GetMapping
    public List<D> getAllResourcesByParent(@PathVariable Map<String, String> pathVars) {
        PI parentId = resolveParentId(pathVars);

        return nestedCrudService.getAllByParent(parentId)
                .stream()
                .map(mapper::mapToDto)
                .toList();
    }

    @GetMapping("/{id}")
    public D getResourceById(@PathVariable Map<String, String> pathVars) throws ResourceNotFoundException {
        PI parentId = resolveParentId(pathVars);
        I id = resolveChildId(pathVars);

        E resource = nestedCrudService.getById(parentId, id);
        return mapper.mapToDto(resource);
    }

    @PostMapping
    public D createResource(@PathVariable Map<String, String> pathVars, @Validated(ValidationGroups.All.class) @RequestBody R request) {
        PI parentId = resolveParentId(pathVars);

        E resource = mapper.mapToEntity(parentId, request);
        E savedResource = nestedCrudService.create(resource);

        afterCreate(parentId, savedResource, request);

        return mapper.mapToDto(savedResource);
    }

    @PutMapping("/{id}")
    public D updateResource(@PathVariable Map<String, String> pathVars, @Validated(ValidationGroups.All.class) @RequestBody R request) throws ResourceNotFoundException {
        PI parentId = resolveParentId(pathVars);
        I id = resolveChildId(pathVars);

        E updatedResource = mapper.updateEntity(id, parentId, request);
        E savedResource = nestedCrudService.update(id, updatedResource);

        afterUpdate(parentId, savedResource, request);

        return mapper.mapToDto(savedResource);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteResource(@PathVariable Map<String, String> pathVars) throws ResourceNotFoundException, ConflictingResourceOperationException {
        PI parentId = resolveParentId(pathVars);
        I id = resolveChildId(pathVars);

        nestedCrudService.delete(parentId, id);
    }

    /**
     * Hook method called after an entity is created.
     * Subclasses can override to perform controller-level post-processing.
     *
     * @param savedEntity the entity that was just saved
     */
    protected void afterCreate(PI parentId, E savedEntity, R request) {
        // default no-op
    }

    /**
     * Hook method called after an entity is updated.
     * Subclasses can override to perform controller-level post-processing.
     *
     * @param savedEntity the entity that was just updated
     */
    protected void afterUpdate(PI parentId, E savedEntity, R request) {
        // default no-op
    }

    @SuppressWarnings("unchecked")
    protected PI resolveParentId(Map<String, String> pathVars) {
        PathParamInfo paramInfo = getParentIdPathParam();
        String rawValue = pathVars.get(paramInfo.name());
        return (PI) convertPathVariable(rawValue, paramInfo.type());
    }

    protected PathParamInfo getParentIdPathParam() {
        return new PathParamInfo("parentId", Long.class);
    }

    @SuppressWarnings("unchecked")
    protected I resolveChildId(Map<String, String> pathVars) {
        PathParamInfo paramInfo = getChildIdPathParam();
        String rawValue = pathVars.get(paramInfo.name());
        return (I) convertPathVariable(rawValue, paramInfo.type());
    }

    protected PathParamInfo getChildIdPathParam() {
        return new PathParamInfo("id", Long.class);
    }

    @SuppressWarnings("unchecked")
    protected <T> T convertPathVariable(String rawValue, Class<T> type) {
        if (rawValue == null) {
            throw new IllegalArgumentException("Missing path variable value for type: " + type.getSimpleName());
        }

        try {
            Object result = switch (type.getSimpleName()) {
                case "Long" -> Long.parseLong(rawValue);
                case "Integer" -> Integer.parseInt(rawValue);
                case "UUID" -> UUID.fromString(rawValue);
                case "String" -> rawValue;
                default -> throw new IllegalArgumentException("Unsupported path variable type: " + type.getName());
            };
            return (T) result;
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid path variable value: '%s' (expected type: %s)"
                    .formatted(rawValue, type.getSimpleName()), ex);
        }
    }
}
