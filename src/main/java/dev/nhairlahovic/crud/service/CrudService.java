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
import org.springframework.data.domain.Sort;
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
        var sort = getFilterCriteria()
                .map(criteria -> Sort.by(criteria.getFilterFields().getKeys().getFirst()).ascending())
                .orElse(Sort.unsorted());

        return repository.findAll(listSpecification(filterValue), sort);
    }

    public Page<T> getByPage(Pageable pageable, String filterValue) {
        return repository.findAll(listSpecification(filterValue), pageable);
    }

    private Specification<T> listSpecification(String filterValue) {
        var filterCriteria = getFilterCriteria();
        if (filterValue == null || filterCriteria.isEmpty()) {
            return baseSpecification();
        }

        return baseSpecification().and(new FilterSpecification<>(filterCriteria.get(), filterValue));
    }

    /**
     * Restriction applied to every list query for this resource, on top of the free-text search
     * filter. Subclasses can override it to hide rows that should never show up in a listing.
     *
     * @return the restriction to apply, unrestricted by default
     */
    protected Specification<T> baseSpecification() {
        return Specification.unrestricted();
    }

    public T getById(ID id) throws ResourceNotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(getResourceType(), id.toString()));
    }

    /**
     * Looks a resource up by id honouring {@link #baseSpecification()}, so rows hidden from the
     * listings are treated as non-existing. Used by the CRUD endpoints; callers that have to reach
     * hidden rows regardless should use {@link #getById(Object)} instead.
     *
     * @param id the id of the resource to look up
     * @return the matching resource
     * @throws ResourceNotFoundException if no resource with that id passes the base specification
     */
    public T getListedById(ID id) throws ResourceNotFoundException {
        return repository.findOne(baseSpecification().and(byIdSpecification(id)))
                .orElseThrow(() -> new ResourceNotFoundException(getResourceType(), id.toString()));
    }

    private Specification<T> byIdSpecification(ID id) {
        return (root, query, builder) -> {
            var entityType = root.getModel();
            var idAttribute = entityType.getId(entityType.getIdType().getJavaType());
            return builder.equal(root.get(idAttribute.getName()), id);
        };
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
        var existingResource = getListedById(id);

        OperationCheck operation = isEditable(resource);
        if (!operation.isAllowed()) {
            throw new ConflictingResourceOperationException(operation.getMessage(), operation.getError());
        }

        beforeUpdate(resource, existingResource);
        return repository.save(resource);
    }

    @Transactional
    public void delete(ID id) throws ConflictingResourceOperationException {
        T entity = this.getListedById(id);

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
     * Called before updating the given entity.
     * Subclasses can override to set default values or enforce business rules.
     *
     * @param entityToUpdate  the incoming entity with new values to be persisted
     * @param existingEntity  the currently persisted entity, useful for preserving fields that should not be overwritten
     */
    protected void beforeUpdate(T entityToUpdate, T existingEntity) {
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
