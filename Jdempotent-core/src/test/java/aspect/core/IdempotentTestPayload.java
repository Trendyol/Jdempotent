package aspect.core;

import com.trendyol.jdempotent.core.annotation.JdempotentIgnore;
import com.trendyol.jdempotent.core.annotation.JdempotentProperty;

public class IdempotentTestPayload {
    private String name;
    @JdempotentIgnore
    private Long age;

    @JdempotentProperty("transactionId")
    private Long eventId;

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

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public void setAge(Long age) {
        this.age = age;
    }
}