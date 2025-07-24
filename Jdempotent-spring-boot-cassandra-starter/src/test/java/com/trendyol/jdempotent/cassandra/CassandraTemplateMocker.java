package com.trendyol.jdempotent.cassandra;

import org.mockito.Mockito;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.cql.CqlOperations;

import static org.mockito.Mockito.when;

public class CassandraTemplateMocker {

    private final CassandraTemplate cassandraTemplate;
    private final CqlOperations cqlOperations;

    private CassandraTemplateMocker() {
        this.cassandraTemplate = Mockito.mock(CassandraTemplate.class);
        this.cqlOperations = Mockito.mock(CqlOperations.class);

        // CassandraTemplate içinden CqlOperations çağrıldığında mock'ı dön
        when(cassandraTemplate.getCqlOperations()).thenReturn(cqlOperations);
    }

    public static CassandraTemplateMocker mock() {
        return new CassandraTemplateMocker();
    }

    public CassandraTemplate getTemplate() {
        return cassandraTemplate;
    }

    public CqlOperations getCqlOperations() {
        return cqlOperations;
    }
}
