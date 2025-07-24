package com.trendyol.jdempotent.cassandra;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.core.mapping.Column;

@Table("idempotent_table")
public class IdempotentEntity {

    @PrimaryKey
    private String id;

    @Column("request")
    private String request;

    @Column("response")
    private String response;

    public IdempotentEntity() {}

    public IdempotentEntity(String id, String request, String response) {
        this.id = id;
        this.request = request;
        this.response = response;
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRequest() { return request; }
    public void setRequest(String request) { this.request = request; }

    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }
}
