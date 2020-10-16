package aspect;

import com.Jdempotent.core.annotation.IdempotentRequestPayload;
import com.Jdempotent.core.annotation.IdempotentResource;
import org.springframework.stereotype.Component;

@Component
public class TestIdempotentResource {

    @IdempotentResource
    public void idempotentMethod(TestPayload testObject){
    }

    @IdempotentResource
    public void idempotentMethodWithThreeParamater(TestPayload testObject,@IdempotentRequestPayload TestPayload anotherObject,TestPayload anotherObject2){
    }

    @IdempotentResource
    public void idempotentMethodWithThreeParamaterAndMultipleIdempotentRequestPayloadAnnotation(TestPayload testObject,@IdempotentRequestPayload TestPayload anotherObject,@IdempotentRequestPayload Object anotherObject2){
    }

    @IdempotentResource
    public void idempotentMethodWithZeroParamater(){
    }

    @IdempotentResource
    public void methodWithTwoParamater(TestPayload testObject,TestPayload anotherObject){
    }

}