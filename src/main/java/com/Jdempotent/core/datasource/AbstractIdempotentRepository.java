package com.Jdempotent.core.datasource;

import com.Jdempotent.core.model.IdempotencyKey;
import com.Jdempotent.core.model.IdempotentRequestResponseWrapper;
import com.Jdempotent.core.model.IdempotentRequestWrapper;
import com.Jdempotent.core.model.IdempotentResponseWrapper;

import java.util.Map;

/**
 * Includes all the methods of IdempotentRequestStore
 *
 */
public abstract class AbstractIdempotentRepository implements IdempotentRepository {

    @Override
    public boolean contains(IdempotencyKey key) {
        return getMap().containsKey(key);
    }

    @Override
    public IdempotentResponseWrapper getResponse(IdempotencyKey key) {
        return getMap().containsKey(key) ? getMap().get(key).getResponse() : null;
    }

    @Override
    public void store(IdempotencyKey key, IdempotentRequestWrapper request) {
        getMap().put(key, new IdempotentRequestResponseWrapper(request));
    }

    @Override
    public void setResponse(IdempotencyKey key, IdempotentRequestWrapper request,
                            IdempotentResponseWrapper idempotentResponse) {
        if (getMap().containsKey(key)) {
            IdempotentRequestResponseWrapper requestResponseWrapper = getMap().get(key);
            requestResponseWrapper.setResponse(idempotentResponse);
            getMap().put(key, requestResponseWrapper);
        }
    }

    @Override
    public void remove(IdempotencyKey key) {
        getMap().remove(key);
    }


    /**
     *
     * @return
     */
    protected abstract Map<IdempotencyKey, IdempotentRequestResponseWrapper> getMap();
}
