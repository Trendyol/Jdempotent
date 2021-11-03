package aspect.core;

import com.trendyol.jdempotent.core.annotation.JdempotentIgnore;

public class IdempotentTestPayload {
    private String name;
    @JdempotentIgnore
    private Long age;

    public IdempotentTestPayload() {
    }

    public IdempotentTestPayload(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}