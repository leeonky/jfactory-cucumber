package com.github.leeonky.jfactory.cucumber;

import io.cucumber.java.Before;

import javax.persistence.EntityTransaction;

public class ClearData {

    @Before
    public void clearDB() {
        EntityTransaction transaction = EntityFactory.entityManager.getTransaction();
        transaction.begin();
        EntityFactory.entityManager.createQuery("delete from Order").executeUpdate();
        EntityFactory.entityManager.createNativeQuery("delete from CART_PRODUCT").executeUpdate();
        EntityFactory.entityManager.createQuery("delete from ProductStock").executeUpdate();
        EntityFactory.entityManager.createQuery("delete from Product").executeUpdate();
        EntityFactory.entityManager.createQuery("delete from Cart").executeUpdate();
        transaction.commit();
        EntityFactory.jpaDataRepository.clear();
    }
}
