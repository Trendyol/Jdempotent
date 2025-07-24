package com.trendyol.jdempotent.cassandra;

import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.trendyol.jdempotent.core.datasource.IdempotentRepository;
import com.trendyol.jdempotent.core.model.IdempotencyKey;
import com.trendyol.jdempotent.core.model.IdempotentRequestWrapper;
import com.trendyol.jdempotent.core.model.IdempotentResponseWrapper;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class CassandraIdempotentRepository implements IdempotentRepository {

    private final CassandraTemplate cassandraTemplate;
    private final CassandraConfigProperties cassandraProperties;

    public CassandraIdempotentRepository(CassandraTemplate cassandraTemplate, CassandraConfigProperties cassandraProperties) {
        this.cassandraTemplate = cassandraTemplate;
        this.cassandraProperties = cassandraProperties;
    }

    @Override
    public boolean contains(IdempotencyKey idempotencyKey) {
        String key = idempotencyKey.getKeyValue();

        SimpleStatement stmt = SimpleStatement.builder("SELECT COUNT(*) FROM idempotent_table WHERE id = ?")
                .addPositionalValue(key)
                .build();

        Long count = cassandraTemplate.getCqlOperations().queryForObject(stmt, Long.class);

        return count > 0;
    }

    @Override
    public IdempotentResponseWrapper getResponse(IdempotencyKey idempotencyKey) {
        String key = idempotencyKey.getKeyValue();

        try {
            // ID'ye göre entity çek
            IdempotentEntity entity = cassandraTemplate.selectOneById(key, IdempotentEntity.class);

            if (entity != null && entity.getResponse() != null) {
                // Eğer setter yoksa constructor ile oluştur
                return new IdempotentResponseWrapper(entity.getResponse());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }


    @Override
    public void store(IdempotencyKey idempotencyKey, IdempotentRequestWrapper idempotentRequestWrapper) {
        IdempotentEntity entity = new IdempotentEntity(
                idempotencyKey.getKeyValue(),
                (String) idempotentRequestWrapper.getRequest(),
                null
        );

        cassandraTemplate.insert(entity);
    }

    @Override
    public void store(IdempotencyKey idempotencyKey, IdempotentRequestWrapper idempotentRequestWrapper, Long ttl, TimeUnit timeUnit) {
        int ttlSeconds = (int) timeUnit.toSeconds(ttl);
        String cql = "INSERT INTO idempotent_table (id, request) VALUES (?, ?) USING TTL ?";

        SimpleStatement stmt = SimpleStatement.builder(cql)
                .addPositionalValues(idempotencyKey.getKeyValue(), idempotentRequestWrapper.getRequest(), ttlSeconds)
                .build();

        cassandraTemplate.getCqlOperations().execute(stmt);
    }

    @Override
    public void remove(IdempotencyKey idempotencyKey) {
        String cql = "DELETE FROM idempotent_table WHERE id = ?";

        SimpleStatement stmt = SimpleStatement.builder(cql)
                .addPositionalValue(idempotencyKey.getKeyValue())
                .build();

        cassandraTemplate.getCqlOperations().execute(stmt);
    }

    @Override
    public void setResponse(IdempotencyKey idempotencyKey, IdempotentRequestWrapper idempotentRequestWrapper, IdempotentResponseWrapper idempotentResponseWrapper) {
        IdempotentEntity entity = new IdempotentEntity(
                idempotencyKey.getKeyValue(),
                (String) idempotentRequestWrapper.getRequest(),
                (String) idempotentResponseWrapper.getResponse()
        );

        cassandraTemplate.insert(entity);
    }

    @Override
    public void setResponse(IdempotencyKey idempotencyKey, IdempotentRequestWrapper idempotentRequestWrapper, IdempotentResponseWrapper idempotentResponseWrapper, Long ttl, TimeUnit timeUnit) {
        int ttlSeconds = (int) timeUnit.toSeconds(ttl);
        String cql = "INSERT INTO idempotent_table (id, request, response) VALUES (?, ?, ?) USING TTL ?";

        SimpleStatement stmt = SimpleStatement.builder(cql)
                .addPositionalValues(idempotencyKey.getKeyValue(), idempotentRequestWrapper.getRequest(), idempotentResponseWrapper.getResponse(), ttlSeconds)
                .build();

        cassandraTemplate.getCqlOperations().execute(stmt);
    }
}