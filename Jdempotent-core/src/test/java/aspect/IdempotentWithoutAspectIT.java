package aspect;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.AopTestUtils;

import static org.junit.Assert.*;

//@RunWith(SpringJUnit4ClassRunner.class)
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestIdempotentResource.class})
public class IdempotentWithoutAspectIT {

    @Autowired
    private TestIdempotentResource testIdempotentResource;


    @Test
    public void given_context_then_run_with_non_aop_context() {
        assertEquals(testIdempotentResource.getClass(), TestIdempotentResource.class);
        assertFalse(AopUtils.isAopProxy(testIdempotentResource));
        assertFalse(AopUtils.isCglibProxy(testIdempotentResource));

        assertEquals(AopProxyUtils.ultimateTargetClass(testIdempotentResource), TestIdempotentResource.class);
        assertEquals(AopTestUtils.getTargetObject(testIdempotentResource).getClass(), TestIdempotentResource.class);
        assertEquals(AopTestUtils.getUltimateTargetObject(testIdempotentResource).getClass(), TestIdempotentResource.class);
    }
}