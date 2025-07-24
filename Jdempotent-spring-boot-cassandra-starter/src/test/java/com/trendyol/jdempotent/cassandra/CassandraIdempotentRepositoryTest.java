package com.trendyol.jdempotent.cassandra;

import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.trendyol.jdempotent.core.model.IdempotencyKey;
import com.trendyol.jdempotent.core.model.IdempotentRequestWrapper;
import com.trendyol.jdempotent.core.model.IdempotentResponseWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.cql.CqlOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CassandraIdempotentRepositoryTest {

    private CassandraTemplate cassandraTemplate;
    private CassandraConfigProperties cassandraProperties;
    private CqlOperations cqlOperations;

    private CassandraIdempotentRepository repository;

    @BeforeEach
    void setUp() {
        CassandraTemplateMocker mocker = CassandraTemplateMocker.mock();
        cassandraTemplate = mocker.getTemplate();
        cqlOperations = mocker.getCqlOperations();
        cassandraProperties = mock(CassandraConfigProperties.class);

        repository = new CassandraIdempotentRepository(cassandraTemplate, cassandraProperties);
    }

    @Test
    void testContains_whenKeyExists_shouldReturnTrue() {
        IdempotencyKey key = new IdempotencyKey("123");
        when(cqlOperations.queryForObject(any(SimpleStatement.class), eq(Long.class))).thenReturn(1L);

        boolean result = repository.contains(key);

        assertTrue(result);
    }

    @Test
    void testContains_whenKeyDoesNotExist_shouldReturnFalse() {
        IdempotencyKey key = new IdempotencyKey("456");
        when(cqlOperations.queryForObject(any(SimpleStatement.class), eq(Long.class))).thenReturn(0L);

        boolean result = repository.contains(key);

        assertFalse(result);
    }

    @Test
    void testGetResponse_whenEntityExists_shouldReturnWrapper() {
        IdempotencyKey key = new IdempotencyKey("789");
        IdempotentEntity entity = new IdempotentEntity("789", "req", "res");

        when(cassandraTemplate.selectOneById("789", IdempotentEntity.class)).thenReturn(entity);

        IdempotentResponseWrapper response = repository.getResponse(key);

        assertNotNull(response);
        assertEquals("res", response.getResponse());
    }

    @Test
    void testGetResponse_whenEntityDoesNotExist_shouldReturnNull() {
        IdempotencyKey key = new IdempotencyKey("nope");

        when(cassandraTemplate.selectOneById("nope", IdempotentEntity.class)).thenReturn(null);

        IdempotentResponseWrapper response = repository.getResponse(key);

        assertNull(response);
    }

    @Test
    void testStore_withoutTTL_shouldInsertEntity() {
        IdempotencyKey key = new IdempotencyKey("abc");
        IdempotentRequestWrapper request = new IdempotentRequestWrapper("req");

        repository.store(key, request);

        ArgumentCaptor<IdempotentEntity> captor = ArgumentCaptor.forClass(IdempotentEntity.class);
        verify(cassandraTemplate).insert(captor.capture());

        IdempotentEntity inserted = captor.getValue();
        assertEquals("abc", inserted.getId());
        assertEquals("req", inserted.getRequest());
        assertNull(inserted.getResponse());
    }

    @Test
    void testStore_withTTL_shouldExecuteStatement() {
        IdempotencyKey key = new IdempotencyKey("abc");
        IdempotentRequestWrapper request = new IdempotentRequestWrapper("req");

        repository.store(key, request, 5L, TimeUnit.MINUTES);

        verify(cqlOperations).execute(any(SimpleStatement.class));
    }

    @Test
    void testRemove_shouldExecuteDeleteStatement() {
        IdempotencyKey key = new IdempotencyKey("delete");

        repository.remove(key);

        verify(cqlOperations).execute(any(SimpleStatement.class));
    }

    @Test
    void testSetResponse_withoutTTL_shouldInsertEntity() {
        IdempotencyKey key = new IdempotencyKey("key");
        IdempotentRequestWrapper req = new IdempotentRequestWrapper("req");
        IdempotentResponseWrapper res = new IdempotentResponseWrapper("res");

        repository.setResponse(key, req, res);

        ArgumentCaptor<IdempotentEntity> captor = ArgumentCaptor.forClass(IdempotentEntity.class);
        verify(cassandraTemplate).insert(captor.capture());

        IdempotentEntity inserted = captor.getValue();
        assertEquals("key", inserted.getId());
        assertEquals("req", inserted.getRequest());
        assertEquals("res", inserted.getResponse());
    }

    @Test
    void testSetResponse_withTTL_shouldExecuteInsertStatement() {
        IdempotencyKey key = new IdempotencyKey("ttl-key");
        IdempotentRequestWrapper req = new IdempotentRequestWrapper("req");
        IdempotentResponseWrapper res = new IdempotentResponseWrapper("res");

        repository.setResponse(key, req, res, 10L, TimeUnit.SECONDS);

        verify(cqlOperations).execute(any(SimpleStatement.class));
    }
}
