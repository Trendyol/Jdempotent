package com.Jdempotent.core.datasource;

import com.Jdempotent.core.model.IdempotencyKey;
import com.Jdempotent.core.model.IdempotentRequestWrapper;
import com.Jdempotent.core.model.IdempotentResponseWrapper;

/**
 *
 * an interface that the functionality required of a request store for idempotent method invocations.
 *

 *
 */
public interface IdempotentRepository {
    /**
     *
     * @param key
     * @return
     */
    boolean contains(IdempotencyKey key);

    /**
     *
     * Checks the cache for an existing call for this request
     *
     * @param key
     * @return
     */
    IdempotentResponseWrapper getResponse(IdempotencyKey key);

    /**
     *
     * @param key
     * @param requestObject
     */
    void store(IdempotencyKey key, IdempotentRequestWrapper requestObject);

    /**
     *
     * @param key
     */
    void remove(IdempotencyKey key);

    /**
     *
     * @param request
     * @param idempotentResponse
     */
    void setResponse(IdempotencyKey key, IdempotentRequestWrapper request, IdempotentResponseWrapper idempotentResponse);
}