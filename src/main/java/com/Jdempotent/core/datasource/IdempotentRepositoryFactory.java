package com.Jdempotent.core.datasource;

import com.Jdempotent.core.datasource.impl.hazelcast.*;
import com.Jdempotent.core.datasource.impl.redis.*;
import com.Jdempotent.core.datasource.impl.InMemoryIdempotentRepository;
import com.Jdempotent.core.constant.RepositoryType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * A factory class that decides which datasource
 * to choose according to the config value
 *
 */
@Configuration
public class IdempotentRepositoryFactory implements ImportBeanDefinitionRegistrar {

    private ConcurrentHashMap<RepositoryType, Class> repositories;

    private IdempotentRepositoryFactory() {
        loadRepositoryMetadata();
    }

    @Value("spring.idempotency.datasource.name")
    private String repositoryConfigName;


    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        BeanDefinition bd = new RootBeanDefinition(IdempotentRepository.class);
        RepositoryType repositoryType = RepositoryType.getRepositoryTypeByValue(repositoryConfigName);
        String repositoryTypeValue = repositoryType.value();

        bd.getConstructorArgumentValues()
                .addGenericArgumentValue(repositoryTypeValue);
        beanDefinitionRegistry.registerBeanDefinition(repositoryTypeValue, bd);
    }

    /**
     *
     * a utility method to select the bean to be raised
     *
     */
    private void loadRepositoryMetadata() {
        repositories = new ConcurrentHashMap<>();
        repositories.put(RepositoryType.REDIS, RedisIdempotentRepository.class);
        repositories.put(RepositoryType.HAZELCAST, HazelcastIdempotentRepository.class);
        repositories.put(RepositoryType.INMEMORY, InMemoryIdempotentRepository.class);
    }

}
