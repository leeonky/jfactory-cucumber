package com.github.leeonky.jfactory.cucumber;

import com.github.leeonky.jfactory.JFactory;
import com.github.leeonky.jfactory.cucumber.spec.Orders;
import com.github.leeonky.jfactory.repo.JPADataRepository;

import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Persistence;

public class EntityFactory extends JFactory {
    public static final EntityManager entityManager = Persistence.createEntityManagerFactory("jfactory-repo").createEntityManager();
    private static final JPADataRepository jpaDataRepository = new JPADataRepository(entityManager);

    public EntityFactory() {
        super(jpaDataRepository);

        register(Orders.订单.class);

        ignoreDefaultValue(propertyWriter -> propertyWriter.getAnnotation(Id.class) != null);
    }
}
