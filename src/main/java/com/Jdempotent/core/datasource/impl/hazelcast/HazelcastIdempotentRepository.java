package com.Jdempotent.core.datasource.impl.hazelcast;

import com.Jdempotent.core.datasource.AbstractIdempotentRepository;
import com.Jdempotent.core.model.IdempotencyKey;
import com.Jdempotent.core.model.IdempotentRequestResponseWrapper;

import java.util.Map;

/**
 *
 * An implementation of the idempotent AbstractIdempotentRepository
 * that uses a distributed hash map from Hazelcast
 *
 */
public class HazelcastIdempotentRepository extends AbstractIdempotentRepository {
    @Override
    protected Map<IdempotencyKey, IdempotentRequestResponseWrapper> getMap() {
        return null;
    }
}
