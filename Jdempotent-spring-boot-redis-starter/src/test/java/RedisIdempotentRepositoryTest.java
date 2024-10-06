import com.trendyol.jdempotent.core.model.IdempotencyKey;
import com.trendyol.jdempotent.core.model.IdempotentRequestResponseWrapper;
import com.trendyol.jdempotent.core.model.IdempotentRequestWrapper;
import com.trendyol.jdempotent.core.model.IdempotentResponseWrapper;
import com.trendyol.jdempotent.redis.configuration.RedisConfigProperties;
import com.trendyol.jdempotent.redis.repository.RedisIdempotentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RedisIdempotentRepositoryTest {


    @InjectMocks
    private RedisIdempotentRepository redisIdempotentRepository;

    @Mock
    private RedisTemplate redisTemplate;

    @Mock
    private RedisConfigProperties redisConfigProperties;

    @Mock
    private ValueOperations<String, IdempotentRequestResponseWrapper> valueOperations;

    @Captor
    private ArgumentCaptor<IdempotentRequestResponseWrapper> captor;

    @BeforeEach
    public void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        redisIdempotentRepository = new RedisIdempotentRepository(redisTemplate,
                redisConfigProperties);
    }

    @Test
    public void given_an_available_object_when_redis_contains_then_return_true() {
        //Given
        IdempotencyKey idempotencyKey = new IdempotencyKey("key");
        var key = new IdempotencyKey("key");
        var wrapper = new IdempotentRequestResponseWrapper(
                new IdempotentRequestWrapper(new Object()));
        when(valueOperations.get(key.getKeyValue())).thenReturn(wrapper);

        //When
        Boolean isContain = redisIdempotentRepository.contains(idempotencyKey);

        //Then
        verify(valueOperations, times(1)).get(idempotencyKey.getKeyValue());
        assertTrue(isContain);
    }

    @Test
    public void given_an_unavailable_object_when_redis_contains_then_return_false() {
        //Given
        IdempotencyKey idempotencyKey = new IdempotencyKey("key1");

        //When
        Boolean isContain = redisIdempotentRepository.contains(idempotencyKey);

        //Then
        verify(valueOperations, times(1)).get(idempotencyKey.getKeyValue());
        assertFalse(isContain);
    }

    @Test
    public void given_an_available_object_when_get_response_then_return_response() {
        //Given
        var key = new IdempotencyKey("key");
        var wrapper = new IdempotentRequestResponseWrapper(
                new IdempotentRequestWrapper(new Object()));
        when(valueOperations.get(key.getKeyValue())).thenReturn(wrapper);
        var t = mock(IdempotentRequestResponseWrapper.class);

        IdempotentResponseWrapper expected = new IdempotentResponseWrapper("testt");
        when(t.getResponse()).thenReturn(expected);
        when(valueOperations.get(key.getKeyValue())).thenReturn(t);

        //When
        IdempotentResponseWrapper response = redisIdempotentRepository.getResponse(key);

        //Then
        verify(t).getResponse();
        assertEquals(response.getResponse(), "testt");
    }

    @Test
    public void given_idempotency_key_and_request_object_when_store_then_set_value_to_redis() {
        //Given
        IdempotencyKey key = new IdempotencyKey("key");
        IdempotentRequestWrapper request = new IdempotentRequestWrapper(123L);
        when(redisConfigProperties.getPersistReqRes()).thenReturn(true);

        //When
        redisIdempotentRepository.store(key, request, 1L, TimeUnit.HOURS);

        //Then
        var argumentCaptor = ArgumentCaptor.forClass(IdempotentRequestResponseWrapper.class);
        verify(valueOperations).set(eq(key.getKeyValue()), argumentCaptor.capture(), eq(1L), eq(TimeUnit.HOURS));
        IdempotentRequestResponseWrapper value = argumentCaptor.getValue();
        assertEquals(value.getRequest().getRequest(), Collections.singletonList(123L));
    }

    @Test
    public void given_ttl_zero_when_store_then_set_value_to_redis_with_property_ttl() {
        //Given
        IdempotencyKey key = new IdempotencyKey("key");
        IdempotentRequestWrapper request = new IdempotentRequestWrapper(123L);
        when(redisConfigProperties.getExpirationTimeHour()).thenReturn(99L);

        //When
        redisIdempotentRepository.store(key, request, 0L, TimeUnit.HOURS);

        //Then
        verify(valueOperations).set(eq(key.getKeyValue()), any(), eq(99L), eq(TimeUnit.HOURS));
    }

    @Test
    public void given_idempotency_key_when_remove_then_delete_redis_key() {
        //Given
        IdempotencyKey key = new IdempotencyKey("key");

        //When
        redisIdempotentRepository.remove(key);

        //Then
        verify(redisTemplate).delete(eq(key.getKeyValue()));
    }

    @Test
    public void given_idempotency_key_and_request_and_response_objects_when_set_response_then_set_response_to_key() {
        //Given
        IdempotencyKey key = new IdempotencyKey("key");
        IdempotentRequestWrapper request = new IdempotentRequestWrapper(123L);
        IdempotentResponseWrapper response = new IdempotentResponseWrapper("response");
        var wrapper = new IdempotentRequestResponseWrapper(
                new IdempotentRequestWrapper(new Object()));
        when(valueOperations.get(key.getKeyValue())).thenReturn(wrapper);
        assertNull(wrapper.getResponse());
        when(redisConfigProperties.getPersistReqRes()).thenReturn(true);

        //When
        redisIdempotentRepository.setResponse(key, request, response, 1L, TimeUnit.HOURS);

        //Then
        var argumentCaptor = ArgumentCaptor.forClass(IdempotentRequestResponseWrapper.class);
        verify(valueOperations).set(eq(key.getKeyValue()), argumentCaptor.capture(), eq(1L), eq(TimeUnit.HOURS));
        IdempotentRequestResponseWrapper value = argumentCaptor.getValue();
        assertEquals(value.getRequest().getRequest(), Collections.singletonList(123L));
        assertEquals(value.getResponse().getResponse(), "response");
        assertEquals(wrapper.getResponse().getResponse(), "response");
    }

    @Test
    public void given_the_idempotence_key_and_the_request_and_response_objects_when_defining_the_response_one_must_save_the_key_without_the_request_and_response_object() {
        //Given
        IdempotencyKey key = new IdempotencyKey("key");
        IdempotentRequestWrapper request = new IdempotentRequestWrapper(123L);
        IdempotentResponseWrapper response = new IdempotentResponseWrapper("response");
        var wrapper = new IdempotentRequestResponseWrapper(
                new IdempotentRequestWrapper(new Object()));
        when(valueOperations.get(key.getKeyValue())).thenReturn(wrapper);
        assertNull(wrapper.getResponse());
        when(redisConfigProperties.getPersistReqRes()).thenReturn(false);

        //When
        redisIdempotentRepository.setResponse(key, request, response, 1L, TimeUnit.HOURS);

        //Then
        var argumentCaptor = ArgumentCaptor.forClass(IdempotentRequestResponseWrapper.class);
        verify(valueOperations).set(eq(key.getKeyValue()), argumentCaptor.capture(), eq(1L), eq(TimeUnit.HOURS));
        IdempotentRequestResponseWrapper value = argumentCaptor.getValue();
        assertNull(value.getRequest());
        assertNull(value.getResponse());
        assertEquals(wrapper.getResponse().getResponse(), "response");
    }
}