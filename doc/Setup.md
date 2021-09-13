# 使用的必要配置

JFactory-Cucumber 通过 Cucumber 依赖注入的方式来获得所必要的唯一依赖 - JFactory。同时，JFactory 通过其依赖的 DataRepository
实现类将创建的数据存取数据库或者其他数据容器中（如mock-server）。

## 依赖注入 JFactory 并使用 JpaDataRepository 来存取数据

Cucumber 支持多种依赖注入框架，具体可以参考 [https://cucumber.io/docs/cucumber/state/](https://cucumber.io/docs/cucumber/state/) 。下面以
Spring Boot 为例来说明如何注入 JFactory。

1. 加入以下的项目依赖并配置 Cucumber

```groovy
testImplementation "io.cucumber:cucumber-java:6.11.0"
testImplementation "io.cucumber:cucumber-spring:6.11.0"
testImplementation 'com.github.leeonky:jfactory-repo-jpa:0.1.3'
testImplementation "org.springframework.boot:spring-boot-starter-test:2.4.4"
testImplementation "org.springframework.boot:spring-boot-starter-data-jpa:2.4.4"
```

```java

@ContextConfiguration(classes = {CucumberConfig.class}, loader = SpringBootContextLoader.class)
@CucumberContextConfiguration
public class Steps {
}

@SpringBootApplication
public class CucumberConfig {
}
```

2. 提供一个 JFactory 的 Spring 创建 Bean 实例方法，并注册工厂类

```java

@Configuration
public class FactoryConfiguration {
    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @Bean
    public JFactory createJFactory() {
        return new JFactory(new JPADataRepository(entityManagerFactory.createEntityManager()))
                .register(Products.商品.class);
    }
}
```

3. 实现一个 Cucumber Hook 来清理数据库中的数据，以确保每个 Cucumber Scenario 的独立性和完整性（可选但强烈推荐）

```java
public class ClearData {
    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private JFactory jFactory;

    @Before
    public void clearDB() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.createQuery("delete from Product").executeUpdate();
        transaction.commit();
        entityManager.close();
        jFactory.getDataRepository().clear();
    }
}
```