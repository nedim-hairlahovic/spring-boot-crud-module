package dev.nhairlahovic.crud.mapper;

import dev.nhairlahovic.crud.model.BaseCompositeKeyEntity;

/**
 * A generic interface for mapping between entities and DTOs in the context of a nested (related) entity structure,
 * where the entity typically uses a composite key consisting of a parent and child identifier.
 * This interface is designed to be implemented for specific use cases where an entity is related to a parent entity,
 * providing methods to map between request DTOs and entities, as well as entities and response DTOs.
 *
 * @param <E>  The type of the entity that this mapper will handle.
 * @param <R>  The type of the request DTO used to create or update the entity.
 * @param <D>  The type of the response DTO used to represent the entity.
 * @param <PI> The type of the identifier of the parent entity.
 * @param <I>  The type of the identifier of the entity.
 */
public interface CompositeKeyResourceMapper<E extends BaseCompositeKeyEntity<I>, R, D, PI, I> {

    /**
     * Maps a request DTO to an entity, associating it with a parent entity.
     *
     * @param parentId The ID of the parent entity.
     * @param request  The request DTO containing the data to be mapped.
     * @return The entity created from the request DTO.
     */
    E mapToEntity(PI parentId, R request);

    /**
     * Maps an entity to a response DTO.
     *
     * @param entity The entity to be mapped.
     * @return The response DTO created from the entity.
     */
    D mapToDto(E entity);

    /**
     * Updates an existing entity with data from the request DTO.
     *
     * @param parentId The ID of the parent entity.
     * @param request  The request DTO containing the updated data.
     * @return The updated entity.
     */
    default E updateEntity(PI parentId, R request) {
        return mapToEntity(parentId, request);
    }
}
