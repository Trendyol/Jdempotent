package aspect.core;

public class IdempotentTestPayload {
    private String name;

    public IdempotentTestPayload(){
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