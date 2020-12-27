package com.github.leeonky.jfactory.cucumber;

import io.cucumber.java.Before;

import javax.persistence.EntityTransaction;

public class ClearData {

    @Before
    public void clearDB() {
        EntityTransaction transaction = EntityFactory.entityManager.getTransaction();
        transaction.begin();
        EntityFactory.entityManager.createQuery("delete from Order").executeUpdate();
        transaction.commit();
    }
}
