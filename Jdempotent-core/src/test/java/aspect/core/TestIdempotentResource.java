package aspect.core;

import com.Jdempotent.core.annotation.IdempotentRequestPayload;
import com.Jdempotent.core.annotation.IdempotentResource;
import org.springframework.stereotype.Component;

@Component
public class TestIdempotentResource {

    @IdempotentResource
    public void idempotentMethod(IdempotentTestPayload testObject) {
    }

    @IdempotentResource(cachePrefix = "TestIdempotentResource")
    public void idempotentMethodThrowingARuntimeException(IdempotentTestPayload testObject) {
        throw new TestException();
    }

    @IdempotentResource(cachePrefix = "TestIdempotentResource")
    public void idempotentMethodWithThreeParamater(@IdempotentRequestPayload IdempotentTestPayload testObject, IdempotentTestPayload anotherObject, IdempotentTestPayload anotherObject2) {
    }

    @IdempotentResource(cachePrefix = "TestIdempotentResource")
    public void idempotentMethodWithThreeParamaterAndMultipleIdempotentRequestPayloadAnnotation(IdempotentTestPayload testObject, @IdempotentRequestPayload IdempotentTestPayload anotherObject, @IdempotentRequestPayload Object anotherObject2) {
    }

    @IdempotentResource(cachePrefix = "TestIdempotentResource")
    public void idempotentMethodWithZeroParamater() {
    }

    @IdempotentResource(cachePrefix = "TestIdempotentResource")
    public void methodWithTwoParamater(IdempotentTestPayload testObject, IdempotentTestPayload anotherObject) {
    }

    @IdempotentResource
    public IdempotentTestPayload idempotentMethodReturnArg(IdempotentTestPayload testObject) {
        return testObject;
    }

}