package aspect.errorcallback;

import com.trendyol.jdempotent.core.aspect.IdempotentAspect;
import com.trendyol.jdempotent.core.datasource.InMemoryIdempotentRepository;
import com.trendyol.jdempotent.core.generator.DefaultKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = { "com.trendyol.jdempotent.core" })
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
