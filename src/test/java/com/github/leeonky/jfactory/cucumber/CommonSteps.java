package com.github.leeonky.jfactory.cucumber;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.messages.internal.com.google.common.base.CaseFormat;
import lombok.SneakyThrows;
import org.picocontainer.annotations.Inject;

import javax.persistence.EntityTransaction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonSteps {

    @Before
    public void clearDB() {
        EntityTransaction transaction = EntityFactory.entityManager.getTransaction();
        transaction.begin();
        EntityFactory.entityManager.createQuery("delete from Order").executeUpdate();
        EntityFactory.entityManager.createNativeQuery("delete from CART_PRODUCT").executeUpdate();
        EntityFactory.entityManager.createQuery("delete from ProductStock").executeUpdate();
        EntityFactory.entityManager.createQuery("delete from Product").executeUpdate();
        EntityFactory.entityManager.createQuery("delete from Cart").executeUpdate();
        EntityFactory.entityManager.createQuery("delete from SnakeCaseProduct").executeUpdate();
        transaction.commit();
        EntityFactory.jpaDataRepository.clear();
    }

    @Inject
    private JData jData;

    @Before("@custom-json-deserializer")
    public void customJsonDeserializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new SimpleModule()
                .addKeyDeserializer(String.class, new SnakeCaseKeyDeserializer()));
        Table.setJsonDeserializer(content -> deserialize(objectMapper, content));
    }

    @After("@custom-json-deserializer")
    public void resetDefaultJsonDeserializer() {
        Table.resetJsonDeserializer();
    }

    @SneakyThrows
    private static Object deserialize(ObjectMapper objectMapper, String content) {
        if (objectMapper.readValue(content, Object.class) instanceof Map)
            return objectMapper.readValue(content, new TypeReference<HashMap<String, Object>>() {});
        else
            return objectMapper.readValue(content, new TypeReference<List<HashMap<String, Object>>>() {});
    }

    private static class SnakeCaseKeyDeserializer extends KeyDeserializer {
        @Override
        public Object deserializeKey(String key, DeserializationContext ctxt) {
            return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, key);
        }
    }
}
