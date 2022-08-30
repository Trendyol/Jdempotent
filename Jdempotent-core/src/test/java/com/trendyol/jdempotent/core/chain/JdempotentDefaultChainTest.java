package com.trendyol.jdempotent.core.chain;

import com.trendyol.jdempotent.core.utils.IdempotentTestPayload;
import com.trendyol.jdempotent.core.model.ChainData;
import com.trendyol.jdempotent.core.model.KeyValuePair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class JdempotentDefaultChainTest {

    @InjectMocks
    private JdempotentDefaultChain jdempotentDefaultChain;

    @Test
    public void should_process_with_no_annotation() throws IllegalAccessException, NoSuchFieldException {
        //Given
        IdempotentTestPayload idempotentTestPayload = new IdempotentTestPayload();
        idempotentTestPayload.setName("value");
        ChainData chainData = new ChainData();
        chainData.setArgs(idempotentTestPayload);
        chainData.setDeclaredField(idempotentTestPayload.getClass().getDeclaredField("name"));

        //When
        KeyValuePair process = jdempotentDefaultChain.process(chainData);

        //Then
        assertEquals("name", process.getKey());
        assertEquals("value", process.getValue());
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
        KeyValuePair process = jdempotentDefaultChain.process(chainData);

        //Then
        assertEquals("eventId", process.getKey());
        assertEquals(1l, process.getValue());
    }
}
