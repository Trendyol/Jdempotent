package aspect.errorcallback;

import aspect.core.IdempotentTestPayload;
import aspect.core.TestException;
import com.trendyol.jdempotent.core.callback.ErrorConditionalCallback;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class TestCustomErrorCallback implements ErrorConditionalCallback {
    @Override
    public boolean onErrorCondition(Object response) {
        if(ObjectUtils.isEmpty(((IdempotentTestPayload) response).getName())){
            return false;
        }
        return ((IdempotentTestPayload) response).getName().equalsIgnoreCase("test");
    }

    @Override
    public RuntimeException onErrorCustomException() {
        return new TestException("Name will not be test");
    }
}
