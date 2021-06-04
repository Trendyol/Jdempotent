import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.ExistsResult;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.UpsertOptions;
import com.trendyol.jdempotent.core.model.IdempotencyKey;
import com.trendyol.jdempotent.core.model.IdempotentRequestResponseWrapper;
import com.trendyol.jdempotent.core.model.IdempotentRequestWrapper;
import com.trendyol.jdempotent.core.model.IdempotentResponseWrapper;
import com.trendyol.jdempotent.couchbase.CouchbaseConfig;
import com.trendyol.jdempotent.couchbase.CouchbaseIdempotentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CouchbaseIdempotentRepositoryTest {
  @InjectMocks
  private CouchbaseIdempotentRepository couchbaseIdempotentRepository;

  @Mock
  private CouchbaseConfig couchbaseConfig;

  @Mock
  private Collection collection;

  @Captor
  private ArgumentCaptor<IdempotentRequestResponseWrapper> captor;

  @Captor
  private ArgumentCaptor<UpsertOptions> upsertOptionCaptor;

  @BeforeEach
  public void setUp() {
    couchbaseIdempotentRepository = new CouchbaseIdempotentRepository(couchbaseConfig,
        collection);
  }

  @Test
  public void given_an_available_object_when_couchbase_contains_then_return_true() {
    //Given
    IdempotencyKey idempotencyKey = new IdempotencyKey("key");
    ExistsResult existsResult = mock(ExistsResult.class);
    when(existsResult.exists()).thenReturn(true);
    when(collection.exists(idempotencyKey.getKeyValue())).thenReturn(existsResult);

    //When
    Boolean isContain = couchbaseIdempotentRepository.contains(idempotencyKey);

    //Then
    verify(collection, times(1)).exists(idempotencyKey.getKeyValue());
    assertTrue(isContain);
  }

  @Test
  public void given_an_available_object_when_couchbase_contains_then_return_false() {
    //Given
    IdempotencyKey idempotencyKey = new IdempotencyKey("key");
    ExistsResult existsResult = mock(ExistsResult.class);
    when(existsResult.exists()).thenReturn(false);
    when(collection.exists(idempotencyKey.getKeyValue())).thenReturn(existsResult);

    //When
    Boolean isContain = couchbaseIdempotentRepository.contains(idempotencyKey);

    //Then
    verify(collection, times(1)).exists(idempotencyKey.getKeyValue());
    assertFalse(isContain);
  }

  @Test
  public void given_an_available_object_when_couchbase_get_response_then_return_expected_idempotent_response_wrapper() {
    //Given
    IdempotencyKey idempotencyKey = new IdempotencyKey("key");
    IdempotentRequestResponseWrapper wrapper = new IdempotentRequestResponseWrapper();
    GetResult getResult = mock(GetResult.class);
    when(getResult.contentAs(IdempotentRequestResponseWrapper.class)).thenReturn(wrapper);
    when(collection.get(idempotencyKey.getKeyValue())).thenReturn(getResult);

    //When
    IdempotentResponseWrapper result = couchbaseIdempotentRepository.getResponse(idempotencyKey);

    //Then
    verify(collection, times(1)).get(idempotencyKey.getKeyValue());
    assertEquals(result, wrapper.getResponse());
  }

  @Test
  public void given_an_available_object_when_couchbase_store_then_collection_insert_once_time() {
    //Given
    IdempotencyKey idempotencyKey = new IdempotencyKey("key");
    IdempotentRequestWrapper wrapper = new IdempotentRequestWrapper();
    IdempotentRequestResponseWrapper responseWrapper = new IdempotentRequestResponseWrapper(wrapper);

    //When
    couchbaseIdempotentRepository.store(idempotencyKey, wrapper);

    //Then
    verify(collection, times(1)).insert(eq(idempotencyKey.getKeyValue()), captor.capture());
    IdempotentRequestResponseWrapper idempotentRequestResponseWrapper = captor.getValue();
    assertEquals(idempotentRequestResponseWrapper.getResponse(), responseWrapper.getResponse());
  }

  @Test
  public void given_an_available_object_when_couchbase_store_with_ttl_and_time_unit_is_days_then_collection_insert_once_time() {
    //Given
    IdempotencyKey idempotencyKey = new IdempotencyKey("key");
    IdempotentRequestWrapper wrapper = new IdempotentRequestWrapper();
    Long ttl = 1L;
    TimeUnit timeUnit = TimeUnit.DAYS;
    Duration duration = Duration.ofDays(1L);
    IdempotentRequestResponseWrapper responseWrapper = new IdempotentRequestResponseWrapper(wrapper);
    /*var mockUpsertOption = Mockito.mockStatic(UpsertOptions.class);
    when(((UpsertOptions)mockUpsertOption)).thenReturn((UpsertOptions) mockUpsertOption);*/

    //When
    couchbaseIdempotentRepository.store(idempotencyKey, wrapper, ttl, timeUnit);

    //Then
    verify(collection, times(1)).upsert(eq(idempotencyKey.getKeyValue()),
        captor.capture(),
        upsertOptionCaptor.capture());
    IdempotentRequestResponseWrapper idempotentRequestResponseWrapper = captor.getValue();
    assertEquals(idempotentRequestResponseWrapper.getResponse(), responseWrapper.getResponse());
    //verify((UpsertOptions)mockUpsertOption, times(1)).expiry(duration);
  }
}