package dev.nhairlahovic.crud.mapper;

/**
 * This interface defines the operations for mapping between entities and their respective
 * Data Transfer Objects (DTOs). It provides methods to map from an entity to its DTO,
 * from a request DTO to an entity, and to update an entity based on its ID and a request DTO.
 * This interface needs to be implemented to handle the specific requirements of the entity
 * and DTO conversions.
 *
 * @param <E> The type of the entity.
 * @param <R> The type of the request DTO.
 * @param <D> The type of the response DTO.
 * @param <I> The type of the identifier of the entity.
 */
public interface ResourceMapper<E, R, D, I> {

    /**
     * Maps an entity to its corresponding DTO.
     *
     * @param entity The entity to be mapped.
     * @return The mapped DTO.
     */
    D mapToDto(E entity);

    /**
     * Maps a request DTO to an entity.
     *
     * @param request The request DTO to be mapped to an entity.
     * @return The mapped entity.
     */
    E mapToEntity(R request);

    /**
     * Updates an existing entity with data from a request DTO.
     *
     * @param id The identifier of the entity to be updated.
     * @param request The request DTO containing the data for the update.
     * @return The updated entity.
     */
    E updateEntity(I id, R request);
}

