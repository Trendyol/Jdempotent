package aspect;

import com.Jdempotent.core.annotation.IdempotentResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.AopTestUtils;

import static org.junit.Assert.*;

//@RunWith(SpringJUnit4ClassRunner.class)
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {IdempotentAspectIT.class, AopContext.class, TestIdempotentResource.class})
public class IdempotentAspectIT { //extends AbstractTestNGSpringContextTests {

    @Autowired
    private TestIdempotentResource testIdempotentResource;

    @Test
    public void given_aop_context_then_run_with_aop_context() throws NoSuchMethodException {
        IdempotentResource idempotentResource = TestIdempotentResource.class.getDeclaredMethods()[0].getAnnotation(IdempotentResource.class);

        assertNotEquals(testIdempotentResource.getClass(), TestIdempotentResource.class);
        assertTrue(AopUtils.isAopProxy(testIdempotentResource));
        assertTrue(AopUtils.isCglibProxy(testIdempotentResource));
        assertNotNull(idempotentResource);

        assertEquals(AopProxyUtils.ultimateTargetClass(testIdempotentResource), TestIdempotentResource.class);
        assertEquals(AopTestUtils.getTargetObject(testIdempotentResource).getClass(), TestIdempotentResource.class);
        assertEquals(AopTestUtils.getUltimateTargetObject(testIdempotentResource).getClass(), TestIdempotentResource.class);
    }
}