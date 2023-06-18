package com.github.leeonky.jfactory.cucumber;

import com.github.leeonky.jfactory.JFactory;
import com.github.leeonky.jfactory.cucumber.factory.*;
import com.github.leeonky.jfactory.repo.JPADataRepository;

import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Persistence;

public class EntityFactory extends JFactory {
    public static final EntityManager entityManager = Persistence.createEntityManagerFactory("jfactory-repo").createEntityManager();
    public static final JPADataRepository jpaDataRepository = new JPADataRepository(entityManager);

    public EntityFactory() {
        super(jpaDataRepository);

        register(Products.商品.class);
        register(Carts.购物车.class);
        register(ProductStocks.库存.class);
        register(Orders.订单.class);
        register(SnakeCaseProducts.SnakeCase商品.class);

        register(Products.ProductFactory.class);
        register(Carts.CartProduct.class);
        register(ProductStocks.Inventory.class);
        register(Orders.OrderFactory.class);

        ignoreDefaultValue(propertyWriter -> propertyWriter.getAnnotation(Id.class) != null);
    }
}
