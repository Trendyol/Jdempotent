package com.trendyol.jdempotent.core.chain;

import com.trendyol.jdempotent.core.model.ChainData;
import com.trendyol.jdempotent.core.model.KeyValuePair;
import com.trendyol.jdempotent.core.utils.IdempotentTestPayload;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class JdempotentNoAnnotationChainTest {

    @InjectMocks
    private JdempotentNoAnnotationChain jdempotentNoAnnotationChain;

    @Mock
    private JdempotentDefaultChain jdempotentDefaultChain;

    @Before
    public void setup(){
        jdempotentNoAnnotationChain.next(jdempotentDefaultChain);
    }

    @Test
    public void should_process_with_no_annotation() throws IllegalAccessException, NoSuchFieldException {
        //Given
        MockData mockData = new MockData();
        ChainData chainData = new ChainData();
        chainData.setArgs(mockData);
        chainData.setDeclaredField(mockData.getClass().getDeclaredField("name"));

        //When
        KeyValuePair process = jdempotentNoAnnotationChain.process(chainData);

        //Then
        assertEquals("name", process.getKey());
        assertEquals(null, process.getValue());
    }


    @Test
    public void should_process_with_another_annotated_property() throws IllegalAccessException, NoSuchFieldException {
        //Given
        IdempotentTestPayload idempotentTestPayload = new IdempotentTestPayload();
        idempotentTestPayload.setEventId(1l);
        ChainData chainData = new ChainData();
        chainData.setArgs(idempotentTestPayload);
        chainData.setDeclaredField(idempotentTestPayload.getClass().getDeclaredField("eventId"));

        //When
        KeyValuePair process = jdempotentNoAnnotationChain.process(chainData);

        //Then
        verify(jdempotentDefaultChain).process(eq(chainData));
    }

    class MockData{
        private String name;
    }
}
