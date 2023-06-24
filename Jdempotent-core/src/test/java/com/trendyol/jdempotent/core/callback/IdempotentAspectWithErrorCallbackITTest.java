package com.trendyol.jdempotent.core.callback;

import com.trendyol.jdempotent.core.constant.CryptographyAlgorithm;
import com.trendyol.jdempotent.core.datasource.InMemoryIdempotentRepository;
import com.trendyol.jdempotent.core.generator.DefaultKeyGenerator;
import com.trendyol.jdempotent.core.model.IdempotencyKey;
import com.trendyol.jdempotent.core.model.IdempotentIgnorableWrapper;
import com.trendyol.jdempotent.core.model.IdempotentRequestWrapper;
import com.trendyol.jdempotent.core.utils.IdempotentTestPayload;
import com.trendyol.jdempotent.core.utils.TestException;
import com.trendyol.jdempotent.core.utils.TestIdempotentResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {IdempotentAspectWithErrorCallbackITTest.class, TestAopWithErrorCallbackContext.class, TestIdempotentResource.class, DefaultKeyGenerator.class, InMemoryIdempotentRepository.class})
public class IdempotentAspectWithErrorCallbackITTest {

    @Autowired
    private TestIdempotentResource testIdempotentResource;

    @Autowired
    private InMemoryIdempotentRepository idempotentRepository;

    @Autowired
    private DefaultKeyGenerator defaultKeyGenerator;

    @Autowired
    private TestCustomErrorCallback testCustomErrorCallback;

    @Test
    public void given_valid_payload_when_trigger_aspect_then_not_throw_custom_error_callback_and_save_repository() throws NoSuchAlgorithmException {
        //given
        IdempotentTestPayload test = new IdempotentTestPayload();
        test.setName("another");
        IdempotentIgnorableWrapper wrapper = new IdempotentIgnorableWrapper();
        wrapper.getNonIgnoredFields().put("name", "another");
        wrapper.getNonIgnoredFields().put("transactionId", null);
        IdempotencyKey idempotencyKey = defaultKeyGenerator.generateIdempotentKey(new IdempotentRequestWrapper(wrapper), "", new StringBuilder(), MessageDigest.getInstance(CryptographyAlgorithm.MD5.value()));

        //when
        testIdempotentResource.idempotentMethodReturnArg(test);

        //then
        assertTrue(idempotentRepository.contains(idempotencyKey));
    }

    @Test(expected = TestException.class)
    public void given_invalid_payload_when_trigger_aspect_then_throw_test_exception_from_custom_error_callback_and_remove_repository() throws NoSuchAlgorithmException {
        //given
        IdempotentTestPayload test = new IdempotentTestPayload();
        test.setName("test");
        IdempotentIgnorableWrapper wrapper = new IdempotentIgnorableWrapper();
        wrapper.getNonIgnoredFields().put("name", "test");
        wrapper.getNonIgnoredFields().put("transactionId", null);
        IdempotencyKey idempotencyKey = defaultKeyGenerator.generateIdempotentKey(new IdempotentRequestWrapper(wrapper), "TestIdempotentResource", new StringBuilder(), MessageDigest.getInstance(CryptographyAlgorithm.MD5.value()));

        //when
        testIdempotentResource.idempotentMethodReturnArg(test);

        //then
        assertFalse(idempotentRepository.contains(idempotencyKey));
    }
}