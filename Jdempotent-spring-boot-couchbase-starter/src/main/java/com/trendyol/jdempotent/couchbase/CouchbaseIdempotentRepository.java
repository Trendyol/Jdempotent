package com.trendyol.jdempotent.couchbase;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.GetOptions;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.UpsertOptions;
import com.trendyol.jdempotent.core.datasource.IdempotentRepository;
import com.trendyol.jdempotent.core.model.IdempotencyKey;
import com.trendyol.jdempotent.core.model.IdempotentRequestResponseWrapper;
import com.trendyol.jdempotent.core.model.IdempotentRequestWrapper;
import com.trendyol.jdempotent.core.model.IdempotentResponseWrapper;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * An implementation of the idempotent IdempotentRepository
 * that uses a distributed hash map from Couchbase
 * <p>
 * That repository needs to store idempotent hash for idempotency check
 */
public class CouchbaseIdempotentRepository implements IdempotentRepository {
    private final CouchbaseConfig couchbaseConfig;
    private final Collection collection;
    private Map<TimeUnit, Function<Long, Duration>> ttlConverter = new HashMap<>();

    public CouchbaseIdempotentRepository(CouchbaseConfig couchbaseConfig, Collection collection) {
        this.couchbaseConfig = couchbaseConfig;
        this.collection = collection;
        this.prepareTtlConverter();
    }


    @Override
    public boolean contains(IdempotencyKey key) {
        return collection.exists(key.getKeyValue()).exists();
    }

    @Override
    public IdempotentResponseWrapper getResponse(IdempotencyKey key) {
        return collection.get(key.getKeyValue(), GetOptions.getOptions().withExpiry(true)).contentAs(IdempotentRequestResponseWrapper.class).getResponse();
    }

    @Override
    public void store(IdempotencyKey key, IdempotentRequestWrapper requestObject) {
        collection.insert(key.getKeyValue(), prepareRequestValue(requestObject));
    }

    @Override
    public void store(IdempotencyKey key, IdempotentRequestWrapper requestObject, Long ttl, TimeUnit timeUnit) {
        Duration ttlDuration = getDurationByTttlAndTimeUnit(ttl, timeUnit);
        collection.upsert(
                key.getKeyValue(), prepareRequestValue(requestObject),
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
            GetResult getResult = collection.get(key.getKeyValue(), GetOptions.getOptions().withExpiry(true));
            IdempotentRequestResponseWrapper requestResponseWrapper = prepareResponseValue(getResult,idempotentResponse);
            collection.upsert(key.getKeyValue(), requestResponseWrapper);
        }
    }

    @Override
    public void setResponse(IdempotencyKey key, IdempotentRequestWrapper request, IdempotentResponseWrapper idempotentResponse, Long ttl, TimeUnit timeUnit) {
        if (contains(key)) {
            GetResult getResult = collection.get(key.getKeyValue(),GetOptions.getOptions().withExpiry(true));
            IdempotentRequestResponseWrapper requestResponseWrapper = prepareResponseValue(getResult,idempotentResponse);
            collection.upsert(
                    key.getKeyValue(),
                    requestResponseWrapper,
                    UpsertOptions.upsertOptions().expiry(getResult.expiry().get()));
        }
    }

    private Duration getDurationByTttlAndTimeUnit(Long ttl, TimeUnit timeUnit) {
        return ttlConverter.get(timeUnit).apply(ttl);
    }

    private void prepareTtlConverter() {
        ttlConverter.put(TimeUnit.DAYS, Duration::ofDays);
        ttlConverter.put(TimeUnit.HOURS, Duration::ofHours);
        ttlConverter.put(TimeUnit.MINUTES, Duration::ofMinutes);
        ttlConverter.put(TimeUnit.SECONDS, Duration::ofSeconds);
        ttlConverter.put(TimeUnit.MILLISECONDS, Duration::ofMillis);
        ttlConverter.put(TimeUnit.MICROSECONDS, Duration::ofMillis);
        ttlConverter.put(TimeUnit.NANOSECONDS, Duration::ofNanos);
    }

    /**
     * Prepares the request value stored in couchbase
     *
     * if persistReqRes set to false,
     * it does not persist related request and response values in couchbase
     * @param request
     * @return
     */
    private IdempotentRequestResponseWrapper prepareRequestValue(IdempotentRequestWrapper request) {
        if (couchbaseConfig.getPersistReqRes()) {
            return new IdempotentRequestResponseWrapper(request);
        }
        return new IdempotentRequestResponseWrapper(null);
    }

    /**
     * Prepares the response value stored in couchbase
     *
     * if persistReqRes set to false,
     * it does not persist related request and response values in redis
     * @param result
     * @param idempotentResponse
     * @return
     */
    private IdempotentRequestResponseWrapper prepareResponseValue(GetResult result,IdempotentResponseWrapper idempotentResponse) {
        IdempotentRequestResponseWrapper requestResponseWrapper = result.contentAs(IdempotentRequestResponseWrapper.class);
        if (couchbaseConfig.getPersistReqRes()) {
            requestResponseWrapper.setResponse(idempotentResponse);
        }
        return requestResponseWrapper;
    }
}
