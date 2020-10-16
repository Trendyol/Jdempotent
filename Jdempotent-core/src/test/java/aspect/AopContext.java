package aspect;

import com.Jdempotent.core.aspect.IdempotentAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = { "com.Jdempotent.core.aspect" })
public class AopContext {

    @Bean
    public IdempotentAspect idempotentAspect () {
        return new IdempotentAspect();
    }

}
