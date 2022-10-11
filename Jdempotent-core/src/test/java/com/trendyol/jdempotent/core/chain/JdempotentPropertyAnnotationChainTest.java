package com.trendyol.jdempotent.core.chain;

import com.trendyol.jdempotent.core.model.ChainData;
import com.trendyol.jdempotent.core.model.KeyValuePair;
import com.trendyol.jdempotent.core.utils.IdempotentTestPayload;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class JdempotentPropertyAnnotationChainTest {

    @InjectMocks
    private JdempotentPropertyAnnotationChain jdempotentPropertyAnnotationChain;

    @Test
    public void should_process_with_no_annotation() throws IllegalAccessException, NoSuchFieldException {
        //Given
        IdempotentTestPayload idempotentTestPayload = new IdempotentTestPayload();
        idempotentTestPayload.setEventId(1l);
        ChainData chainData = new ChainData();
        chainData.setArgs(idempotentTestPayload);
        chainData.setDeclaredField(idempotentTestPayload.getClass().getDeclaredField("eventId"));

        //When
        KeyValuePair process = jdempotentPropertyAnnotationChain.process(chainData);

        //Then
        assertEquals("transactionId", process.getKey());
        assertEquals(1l, process.getValue());
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
        KeyValuePair process = jdempotentPropertyAnnotationChain.process(chainData);

        //Then
        assertEquals("transactionId", process.getKey());
        assertEquals(1l, process.getValue());
    }
}
