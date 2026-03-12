package dev.nhairlahovic.crud.mapper;

import dev.nhairlahovic.crud.exception.PatchException;
import dev.nhairlahovic.crud.model.BaseEntity;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

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
public interface ResourceMapper<E extends BaseEntity<I>, R, D, I> {

    ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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
     * @param id      The identifier of the entity to be updated.
     * @param request The request DTO containing the data for the update.
     * @return The updated entity.
     */
    default E updateEntity(I id, R request) {
        E entity = mapToEntity(request);
        entity.setId(id);
        return entity;
    }

    /**
     * Applies a partial update to an existing entity using only the fields present in the raw JSON request.
     * Deserializes the request into an intermediate entity via {@link #mapToEntity}, then copies
     * only the present patchable fields onto the target entity. Explicit {@code null} values clear the field;
     * absent fields are left unchanged.
     *
     * @param resource        The existing entity to patch.
     * @param rawRequest      The raw JSON patch request used to determine which fields are present.
     * @param patchableFields Map of DTO field name to entity field name for fields eligible for patching.
     * @return The patched entity.
     */
    default E patchEntity(E resource, JsonNode rawRequest, Map<String, String> patchableFields) {
        try {
            R patchRequest = OBJECT_MAPPER.treeToValue(rawRequest, requestType());
            E patchSource = mapToEntity(patchRequest);
            Class<?> clazz = resource.getClass();

            for (Map.Entry<String, String> entry : patchableFields.entrySet()) {
                var dtoFieldName = entry.getKey();
                var entityFieldName = entry.getValue();

                if (!rawRequest.has(dtoFieldName)) {
                    continue;
                }

                try {
                    var field = clazz.getDeclaredField(entityFieldName);
                    field.setAccessible(true);
                    var patchedValue = rawRequest.get(dtoFieldName).isNull() ? null : field.get(patchSource);
                    field.set(resource, patchedValue);
                } catch (Exception ex) {
                    throw new PatchException("Failed to patch field '" + entityFieldName);
                }
            }
        } catch (JacksonException ex) {
            throw new PatchException("Invalid patch request");
        }

        return resource;
    }

    @SuppressWarnings("unchecked")
    default Class<R> requestType() {
        for (Type genericInterface : getClass().getGenericInterfaces()) {
            if (genericInterface instanceof ParameterizedType pt && pt.getRawType() == ResourceMapper.class) {
                return (Class<R>) pt.getActualTypeArguments()[1];
            }
        }
        throw new IllegalStateException("Cannot determine request type for " + getClass().getName());
    }
}
