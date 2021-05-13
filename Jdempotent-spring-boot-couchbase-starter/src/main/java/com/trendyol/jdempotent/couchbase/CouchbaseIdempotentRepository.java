package com.trendyol.jdempotent.couchbase;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.UpsertOptions;
import com.trendyol.jdempotent.core.datasource.IdempotentRepository;
import com.trendyol.jdempotent.core.model.IdempotencyKey;
import com.trendyol.jdempotent.core.model.IdempotentRequestResponseWrapper;
import com.trendyol.jdempotent.core.model.IdempotentRequestWrapper;
import com.trendyol.jdempotent.core.model.IdempotentResponseWrapper;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * An implementation of the idempotent IdempotentRepository
 * that uses a distributed hash map from Couchbase
 * <p>
 * That repository needs to store idempotent hash for idempotency check
 */
public class CouchbaseIdempotentRepository implements IdempotentRepository {
    private final CouchbaseConfig couchbaseConfig;
    private final Collection collection;

    public CouchbaseIdempotentRepository(CouchbaseConfig couchbaseConfig, Collection collection) {
        this.couchbaseConfig = couchbaseConfig;
        this.collection = collection;
    }


    @Override
    public boolean contains(IdempotencyKey key) {
        return collection.exists(key.getKeyValue()).exists();
    }

    @Override
    public IdempotentResponseWrapper getResponse(IdempotencyKey key) {
        return collection.get(key.getKeyValue()).contentAs(IdempotentRequestResponseWrapper.class).getResponse();
    }

    @Override
    public void store(IdempotencyKey key, IdempotentRequestWrapper requestObject) {
        collection.insert(key.getKeyValue(),new IdempotentRequestResponseWrapper(requestObject));
    }

    @Override
    public void store(IdempotencyKey key, IdempotentRequestWrapper requestObject, Long ttl, TimeUnit timeUnit) {
        Duration ttlDuration = getDurationByTttlAndTimeUnit(ttl, timeUnit);
        collection.upsert(
                key.getKeyValue(), new IdempotentRequestResponseWrapper(requestObject),
                UpsertOptions.upsertOptions().expiry(ttlDuration)
        );
    }

    @Override
    public void remove(IdempotencyKey key) {
        collection.remove(key.getKeyValue());
    }

    @Override
    public void setResponse(IdempotencyKey key, IdempotentRequestWrapper request, IdempotentResponseWrapper idempotentResponse) {
        if (contains(key)) {
            IdempotentRequestResponseWrapper requestResponseWrapper = collection.get(key.getKeyValue()).contentAs(IdempotentRequestResponseWrapper.class);
            requestResponseWrapper.setResponse(idempotentResponse);
            collection.upsert(key.getKeyValue(), requestResponseWrapper);
        }
    }

    @Override
    public void setResponse(IdempotencyKey key, IdempotentRequestWrapper request, IdempotentResponseWrapper idempotentResponse, Long ttl, TimeUnit timeUnit) {
        if (contains(key)) {
            IdempotentRequestResponseWrapper requestResponseWrapper = collection.get(key.getKeyValue()).contentAs(IdempotentRequestResponseWrapper.class);
            requestResponseWrapper.setResponse(idempotentResponse);
            collection.upsert(key.getKeyValue(), requestResponseWrapper);
        }
    }

    private Duration getDurationByTttlAndTimeUnit(Long ttl, TimeUnit timeUnit) {
        if (TimeUnit.DAYS.equals(timeUnit)) {
            return Duration.ofDays(ttl);
        } else if (TimeUnit.HOURS.equals(timeUnit)) {
            return Duration.ofHours(ttl);
        } else { //TODO look here
            return Duration.ofMillis(ttl);
        }
    }
}
