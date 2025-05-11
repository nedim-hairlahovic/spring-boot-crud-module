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
            throw new ConflictingResourceOperationException("Failed to create resource. Reason: " + operation.getMessage());
        }

        return repository.save(resource);
    }

    @Transactional
    public E update(ID id, E resource) {
        E existingResource = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(getResourceType(), id.toString()));

        OperationCheck operation = isEditable(id, resource);
        if (!operation.isAllowed()) {
            throw new ConflictingResourceOperationException("Failed to edit resource. Reason: " + operation.getMessage());
        }

        preserveSomeData(resource, existingResource);
        cleanSomeData(existingResource);

        return repository.save(resource);
    }

    public void delete(PI parentId, ID id) {
        E entity = this.getById(parentId, id);

        OperationCheck operation = isDeletable(entity);
        if (!operation.isAllowed()) {
            throw new ConflictingResourceOperationException("Failed to delete resource. Reason: " + operation.getMessage());
        }

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
     * Preserves fields from the existing resource that should remain unchanged during the update.
     *
     * @param resource the new resource with updates
     * @param existingResource the current resource with data to preserve
     */
    protected void preserveSomeData(E resource, E existingResource) {
    }

    /**
     * Removes outdated or unnecessary data from the existing resource before updating.
     *
     * @param existingResource the current resource to clean
     */
    protected void cleanSomeData(E existingResource) {
    }
}
