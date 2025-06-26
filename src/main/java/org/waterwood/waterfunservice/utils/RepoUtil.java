package org.waterwood.waterfunservice.utils;

import org.springframework.data.jpa.repository.JpaRepository;
import org.waterwood.waterfunservice.DTO.common.ErrorType;
import org.waterwood.waterfunservice.DTO.common.ResponseCode;
import org.waterwood.waterfunservice.DTO.common.result.OpResult;

import java.util.Optional;
import java.util.function.Function;

public class RepoUtil {
    public static <T, R> OpResult<R> findAndApply(
            Optional<T> optional,
            Function<T, OpResult<R>> onFound,
            OpResult<R> notFoundResult) {
        return optional.map(onFound).orElse(notFoundResult);
    }

    /**
     * Checks if an entity exists in the repository by its ID.
     * @param repo the repository to check
     * @param id the ID of the entity to check
     * @param entityName the name of the entity for error messages
     * @param notFoundCode the response code to return if the entity is not found
     * @param onFound a function to apply if the entity is found, returning an OperationResult
     * @return an OperationResult indicating success or failure
     * @param <E> the type of the entity
     * @param <ID> the type of the entity ID
     * @param <R> the type of return
     */
    public static <E, ID, R> OpResult<R> checkEntityExistsWithId(
            JpaRepository<E, ID> repo, ID id, String entityName, ResponseCode notFoundCode,
            Function<E, OpResult<R>> onFound) {
        return repo.findById(id)
                .map(onFound)
                .orElse(OpResult.<R>builder()
                        .trySuccess(false)
                        .errorType(ErrorType.CLIENT)
                        .responseCode(notFoundCode)
                        .message(entityName + " with ID " + id + " not found.")
                        .build());
    }
}
