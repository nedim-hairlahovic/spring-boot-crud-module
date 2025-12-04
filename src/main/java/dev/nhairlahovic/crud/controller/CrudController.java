package dev.nhairlahovic.crud.controller;

import com.fasterxml.jackson.databind.JsonNode;
import dev.nhairlahovic.crud.annotation.Patchable;
import dev.nhairlahovic.crud.exception.ConflictingResourceOperationException;
import dev.nhairlahovic.crud.exception.ResourceNotFoundException;
import dev.nhairlahovic.crud.mapper.ResourceMapper;
import dev.nhairlahovic.crud.model.BaseEntity;
import dev.nhairlahovic.crud.model.PageDto;
import dev.nhairlahovic.crud.service.CrudService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This abstract class provides a generic CRUD (Create, Read, Update, Delete) controller.
 * It is designed to work with various types of entities and DTOs (Data Transfer Objects).
 * It must be extended by concrete classes that specify the types of the entity, request DTO,
 * response DTO, and the ID type.
 *
 * @param <E> The type of the entity that this controller will manage.
 * @param <R> The type of the request DTO.
 * @param <D> The type of the response DTO.
 * @param <I> The type of the ID of the entity.
 */
@RequiredArgsConstructor
public abstract class CrudController<E extends BaseEntity<I>, R, D, I> {

    protected final CrudService<E, I> crudService;
    protected final ResourceMapper<E, R, D, I> mapper;

    @GetMapping("/all")
    public List<D> getAllResources(@RequestParam(required = false) String search) {
        return crudService.getAll(search)
                .stream()
                .map(mapper::mapToDto)
                .toList();
    }

    @GetMapping
    public PageDto<D> getPaginatedResources(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
                                            @RequestParam(required = false) String search) {
        Page<D> paginatedResources = crudService.getByPage(pageable, search).map(mapper::mapToDto);
        return PageDto.of(paginatedResources);
    }

    @GetMapping("/{id}")
    public D getResourceById(@PathVariable("id") I id) throws ResourceNotFoundException {
        E resource = crudService.getById(id);
        return mapper.mapToDto(resource);
    }

    @PostMapping
    public D createResource(@Valid @RequestBody R request) {
        E resource = mapper.mapToEntity(request);
        E savedResource = crudService.create(resource);
        return mapper.mapToDto(savedResource);
    }

    @PutMapping("/{id}")
    public D updateResource(@PathVariable("id") I id, @Valid @RequestBody R request) throws ResourceNotFoundException {
        E resource = mapper.updateEntity(id, request);
        E updatedResource = crudService.update(id, resource);
        return mapper.mapToDto(updatedResource);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteResource(@PathVariable("id") I id) throws ResourceNotFoundException, ConflictingResourceOperationException {
        crudService.delete(id);
    }

    @PatchMapping("/{id}")
    public D patchResource(@PathVariable("id") I id, @RequestBody JsonNode request) throws ResourceNotFoundException {
        Set<String> patchableFields = getPatchableFields();
        if (patchableFields.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "HTTP method PATCH is not supported for this resource.");
        }

        E resource = crudService.getById(id);
        E patchedResource = mapper.patchEntity(resource, request, patchableFields);
        E savedResource = crudService.update(id, patchedResource);
        return mapper.mapToDto(savedResource);
    }

    protected Set<String> getPatchableFields() {
        Class<R> requestClass = getRequestClass();

        return Arrays.stream(requestClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Patchable.class))
                .map(Field::getName)
                .collect(Collectors.toSet());
    }

    @SuppressWarnings("unchecked")
    protected Class<R> getRequestClass() {
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<R>) type.getActualTypeArguments()[1]; // index 1 = R
    }
}
