package aspect.errorcallback;

import com.Jdempotent.core.aspect.IdempotentAspect;
import com.Jdempotent.core.datasource.InMemoryIdempotentRepository;
import com.Jdempotent.core.generator.DefaultKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = { "com.Jdempotent.core" })
public class TestAopWithErrorCallbackContext {

    @Bean
    public IdempotentAspect idempotentAspect (InMemoryIdempotentRepository inMemoryIdempotentRepository, DefaultKeyGenerator defaultKeyGenerator, TestCustomErrorCallback testCustomErrorCallback) {
        return new IdempotentAspect(inMemoryIdempotentRepository,testCustomErrorCallback, defaultKeyGenerator);
    }

    @Bean
    public InMemoryIdempotentRepository inMemoryIdempotentRepository(){
        return new InMemoryIdempotentRepository();
    }

    @Bean
    public DefaultKeyGenerator defaultKeyGenerator(){
        return new DefaultKeyGenerator();
    }

    @Bean
    public TestCustomErrorCallback testCustomErrorCallback(){
        return new TestCustomErrorCallback();
    }

}
