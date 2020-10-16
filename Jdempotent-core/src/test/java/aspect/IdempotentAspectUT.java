package aspect;

import com.Jdempotent.core.annotation.IdempotentResource;
import com.Jdempotent.core.aspect.IdempotentAspect;
import com.Jdempotent.core.datasource.IdempotentRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Method;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestIdempotentResource.class})
public class IdempotentAspectUT {

    @InjectMocks
    private IdempotentAspect idempotentAspect;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private IdempotentRepository idempotentRepository;


    @Test
    public void given_new_payload_when_key_not_in_repository_and_method_has_one_arg_then_will_store_repository() throws Throwable {

        //given
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        MethodSignature signature = mock(MethodSignature.class);
        Method method = TestIdempotentResource.class.getMethod("idempotentMethod", TestPayload.class);

        IdempotentResource idempotentResource = mock(IdempotentResource.class);
        TestPayload payload = new TestPayload("payload");
        TestIdempotentResource testIdempotentResource = mock(TestIdempotentResource.class);

        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{payload});
        when(signature.getMethod()).thenReturn(method);
        when(joinPoint.getTarget()).thenReturn(testIdempotentResource);
        when(joinPoint.getTarget().getClass().getSimpleName()).thenReturn("TestIdempotentResource");
        //        when(AnnotationUtils.getAnnotation(method, FlipBean.class)).thenReturn(flipBean);
        //    when(TestIdempotentResource.class.getDeclaredMethods()[0].getAnnotation(IdempotentResource.class)).thenReturn(idempotentResource);

        when(idempotentRepository.contains(any())).thenReturn(false);
        //  when(idempotentRepository.getResponse(any())).thenReturn(new IdempotentResponseWrapper(payload));
        //   when(idempotentRepository.store(any()))
        //   when(idempotentRepository.remove(any()))

        //when
        idempotentAspect.execute(joinPoint);

        //then
        verify(joinPoint, times(2)).getSignature();
        verify(signature).getMethod();
        verify(joinPoint).getTarget();
        verify(idempotentRepository, times(1)).store(any(), any());
        verify(joinPoint).proceed();
        verify(idempotentRepository, times(1)).setResponse(any(), any(), any());
    }

    @Test
    public void given_multiple_new_payload_with_idempotent_payload_annotation_when_key_not_in_repository_and_method_has_more_than_one_args_then_will_store_repository() throws Throwable {

        //given
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        MethodSignature signature = mock(MethodSignature.class);
        Method method = TestIdempotentResource.class.getMethod("idempotentMethodWithThreeParamater", TestPayload.class,TestPayload.class,TestPayload.class);

        IdempotentResource idempotentResource = mock(IdempotentResource.class);
        TestPayload payload1 = new TestPayload("payload-1");
        TestPayload payload2 = new TestPayload("payload-2");
        TestPayload payload3 = new TestPayload("payload-3");
        TestIdempotentResource testIdempotentResource = mock(TestIdempotentResource.class);

        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{payload1,payload2,payload3});
        when(signature.getMethod()).thenReturn(method);
        when(joinPoint.getTarget()).thenReturn(testIdempotentResource);
        when(joinPoint.getTarget().getClass().getSimpleName()).thenReturn("TestIdempotentResource");
        when(joinPoint.getTarget().getClass().getMethod(any(),any())).thenReturn("TestIdempotentResource");


        pjp.getTarget().getClass().getMethod(methodName, parameterTypes).getParameterAnnotations()
        //        when(AnnotationUtils.getAnnotation(method, FlipBean.class)).thenReturn(flipBean);
        //    when(TestIdempotentResource.class.getDeclaredMethods()[0].getAnnotation(IdempotentResource.class)).thenReturn(idempotentResource);

        when(idempotentRepository.contains(any())).thenReturn(false);
        //  when(idempotentRepository.getResponse(any())).thenReturn(new IdempotentResponseWrapper(payload));
        //   when(idempotentRepository.store(any()))
        //   when(idempotentRepository.remove(any()))

        //when
        idempotentAspect.execute(joinPoint);



        //then
        verify(joinPoint, times(2)).getSignature();
        verify(signature).getMethod();
        verify(joinPoint).getTarget();
        verify(idempotentRepository, times(1)).store(any(), any());
        verify(joinPoint).proceed();
        verify(idempotentRepository, times(1)).setResponse(any(), any(), any());
    }
}
