package dev.nhairlahovic.crud.service;

import dev.nhairlahovic.crud.exception.ConflictingResourceOperationException;
import dev.nhairlahovic.crud.exception.ResourceNotFoundException;
import dev.nhairlahovic.crud.model.OperationCheck;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * This abstract class provides a simplified generic CRUD service for entities related to a parent entity.
 * It handles basic CRUD operations (Create, Read, Update, Delete) without pagination or filtering.
 *
 * @param <P>  The parent entity type that this service will manage.
 * @param <E>  The related entity type that this service will manage.
 * @param <PI> The type of the ID of the parent entity.
 * @param <ID> The type of the ID of the related entity.
 */
@RequiredArgsConstructor
public abstract class NestedCrudService<P, E, PI, ID> {

    protected final JpaRepository<E, ID> repository;
    protected final JpaRepository<P, PI> parentRepository;

    public abstract String getResourceType();

    public abstract String getParentResourceType();

    protected abstract List<E> findByParent(P parent);

    protected abstract Optional<E> findByIdAndParent(ID id, P parent);

    public List<E> getAllByParent(PI parentId) {
        P parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException(getParentResourceType(), parentId.toString()));

        return findByParent(parent);
    }

    public E getById(PI parentId, ID id) {
        P parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException(getParentResourceType(), parentId.toString()));

        return findByIdAndParent(id, parent)
                .orElseThrow(() -> new ResourceNotFoundException(getResourceType(), id.toString()));
    }

    public E create(E resource) {
        OperationCheck operation = isCreatable(resource);
        if (!operation.isAllowed()) {
            throw new ConflictingResourceOperationException(operation.getMessage(), operation.getError());
        }

        E savedResource = repository.save(resource);

        afterCreate(savedResource);

        return savedResource;
    }

    @Transactional
    public E update(ID id, E resource) {
        E existingResource = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(getResourceType(), id.toString()));

        OperationCheck operation = isEditable(id, resource);
        if (!operation.isAllowed()) {
            throw new ConflictingResourceOperationException(operation.getMessage(), operation.getError());
        }

        beforeUpdate(resource, existingResource);

        E updatedResource = repository.save(resource);

        afterUpdate(updatedResource);

        return updatedResource;
    }

    @Transactional
    public void delete(PI parentId, ID id) {
        E entity = this.getById(parentId, id);

        OperationCheck operation = isDeletable(entity);
        if (!operation.isAllowed()) {
            throw new ConflictingResourceOperationException(operation.getMessage(), operation.getError());
        }

        beforeDelete(entity);
        repository.delete(entity);
    }

    protected OperationCheck isCreatable(E resource) {
        return OperationCheck.permitted();
    }

    protected OperationCheck isEditable(ID id, E resource) {
        return OperationCheck.permitted();
    }

    protected OperationCheck isDeletable(E resource) {
        return OperationCheck.permitted();
    }

    /**
     * Called after a new entity is successfully created and saved.
     * Subclasses can override this to perform related actions (e.g., updating related data).
     *
     * @param createdEntity the entity that was created
     */
    protected void afterCreate(E createdEntity) {
        // default no-op
    }

    /**
     * Called before updating an entity.
     * Subclasses can override to preserve data from the existing entity
     * or clean up the new entity before saving.
     *
     * @param newResource      the new resource with updates
     * @param existingResource the current resource loaded from the DB
     */
    protected void beforeUpdate(E newResource, E existingResource) {
        // default no-op
    }

    /**
     * Called after an existing entity is successfully updated and saved.
     * Subclasses can override this to perform related actions (e.g., syncing relationships).
     *
     * @param updatedEntity the entity that was updated
     */
    protected void afterUpdate(E updatedEntity) {
        // default no-op
    }

    /**
     * Called before deleting the given entity.
     * Subclasses can override to clean up related data or enforce business rules.
     *
     * @param entity the entity to be deleted
     */
    protected void beforeDelete(E entity) {
        // default no-op
    }
}
