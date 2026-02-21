package dev.nhairlahovic.crud.service;

import dev.nhairlahovic.crud.exception.ConflictingResourceOperationException;
import dev.nhairlahovic.crud.exception.ResourceNotFoundException;
import dev.nhairlahovic.crud.filter.FilterCriteria;
import dev.nhairlahovic.crud.filter.FilterSpecification;
import dev.nhairlahovic.crud.model.OperationCheck;
import dev.nhairlahovic.crud.repository.JpaFilterRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

/**
 * This abstract class provides a generic CRUD service.
 * It uses JPA (Java Persistence API) for data access and manipulation,
 * and is designed to work with a variety of entity types.
 * Concrete implementations of this service should specify the entity type
 * and the type of its identifier.
 *
 * @param <T>  The entity type that this service will manage. This type should
 *             be a JPA entity.
 * @param <ID> The type of the identifier (ID) of the entity.
 */
@RequiredArgsConstructor
public abstract class CrudService<T, ID> {

    protected final JpaFilterRepository<T, ID> repository;

    public abstract String getResourceType();

    public abstract Optional<FilterCriteria> getFilterCriteria();

    public List<T> getAll() {
        return getAll(null);
    }

    public List<T> getAll(String filterValue) {
        if (filterValue == null || getFilterCriteria().isEmpty()) {
            return repository.findAll();
        }

        Specification<T> filterSpec = new FilterSpecification<>(getFilterCriteria().get(), filterValue);
        return repository.findAll(filterSpec);
    }

    public Page<T> getByPage(Pageable pageable, String filterValue) {
        if (filterValue == null || getFilterCriteria().isEmpty()) {
            return repository.findAll(pageable);
        }

        Specification<T> filterSpec = new FilterSpecification<>(getFilterCriteria().get(), filterValue);
        return repository.findAll(filterSpec, pageable);
    }

    public T getById(ID id) throws ResourceNotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(getResourceType(), id.toString()));
    }

    public T create(T resource) {
        OperationCheck operation = isCreatable(resource);
        if (!operation.isAllowed()) {
            throw new ConflictingResourceOperationException(operation.getMessage(), operation.getError());
        }

        beforeCreate(resource);
        return repository.save(resource);
    }

    public T update(ID id, T resource) throws ResourceNotFoundException {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(getResourceType(), id.toString());
        }

        OperationCheck operation = isEditable(resource);
        if (!operation.isAllowed()) {
            throw new ConflictingResourceOperationException(operation.getMessage(), operation.getError());
        }

        return repository.save(resource);
    }

    @Transactional
    public void delete(ID id) throws ConflictingResourceOperationException {
        T entity = this.getById(id);

        OperationCheck operation = isDeletable(entity);
        if (!operation.isAllowed()) {
            throw new ConflictingResourceOperationException(operation.getMessage(), operation.getError());
        }

        beforeDelete(entity);
        repository.delete(entity);
    }

    /**
     * Called before creating the given entity.
     * Subclasses can override to set default values or enforce business rules.
     *
     * @param entity the entity to be created
     */
    protected void beforeCreate(T entity) {
        // default no-op
    }

    /**
     * Called before deleting the given entity.
     * Subclasses can override to clean up related data or enforce business rules.
     *
     * @param entity the entity to be deleted
     */
    protected void beforeDelete(T entity) {
        // default no-op
    }

    protected OperationCheck isCreatable(T resource) {
        return OperationCheck.permitted();
    }

    protected OperationCheck isEditable(T resource) {
        return OperationCheck.permitted();
    }

    protected OperationCheck isDeletable(T resource) {
        return OperationCheck.permitted();
    }

    public List<T> getByIdIn(List<ID> ids) {
        return repository.findAllById(ids);
    }
}
