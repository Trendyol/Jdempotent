package aspect.core;

import com.trendyol.jdempotent.core.annotation.IdempotentIgnore;
import com.trendyol.jdempotent.core.annotation.JdempotentId;

public class IdempotentTestPayload {
    private String name;
    @IdempotentIgnore
    private Long age;

    @JdempotentId
    private String jdempotentId;

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