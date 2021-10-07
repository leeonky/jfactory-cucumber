# 使用表格方式来准备数据

用表格的方式来准备数据，是一种最常用的方法。指的是用一些已通过 `Spec` 注册过的工厂类，利用 Cucumber 提供的表格机制，创建出一个或者多个对象。

## 准备单一对象

假如有下面的实体类，并且工厂类已经注册：

```java

@Entity
@Getter
@Setter
@Table(name = "products")
public class Product {
    @Id
    private long id;
    private String name;
    private String color;
}

public class Products {
    public static class 商品 extends Spec<Product> {
    }
}
```

当用如下的 Cucumber Step 来创建对象时：

```gherkin
存在"商品"：
| name |
| book |
```

或者，当用如下的 Api 来创建对象时：

```java
jData.prepare("商品",new HashMap<String, Object>(){{
        put("name","book");
        }});
```

那么都将如下数据创建到数据库中：

```gherkin
products
| id | name | color   |
| 1  | book | color#1 |
```

值得注意的是，对于其他未显式设置值的属性（如id和color），会被设置默认值。具体默认值的规则，由 [JFactory](https://github.com/leeonky/jfactory/blob/master/README.md)
决定。

### 转置表格

Cucumber 支持 [转置表格](https://javadoc.io/doc/io.cucumber/cucumber-java/6.11.0/io/cucumber/java/Transpose.html)
，JFactory-Cucumber 也同样支持，方法是在第一个字段（如下面的name）前面加一个单引号 `'`。

比如还是用上面的实体类和工厂类，当用如下的 Cucumber Step 来创建对象时：

```gherkin
存在"商品"：
| 'name | book | bicycle |
| color | red  | white   |
```

那么会将如下数据创建到数据库中：

```gherkin
products
| id | name    | color |
| 1  | book    | red   |
| 2  | bicycle | white |
```

## 准备多级对象

假如有下面的实体类，并且工厂类已经注册：

```java

@Entity
@Getter
@Setter
@Table(name = "carts")
public class Cart {

    @Id
    private long id;
    private String customer;

    @OneToMany
    private List<Product> products = new ArrayList<>();
}

@Entity
@Getter
@Setter
@Table(name = "products")
public class Product {

    @Id
    private long id;
    private String name;

    @OneToMany(mappedBy = "product")
    private List<ProductStock> stocks = new ArrayList<>();
}

@Entity
@Getter
@Setter
@Table(name = "product_stocks")
public class ProductStock {

    @Id
    private long id;

    @ManyToOne
    private Product product;

    private String size;
    private int count;
}

public class Carts {
    public static class 购物车 extends Spec<Cart> {
    }
}

public class Products {
    public static class 商品 extends Spec<Product> {
    }
}

public class ProductStocks {
    public static class 库存 extends Spec<ProductStock> {
    }
}
```

当用如下的 Cucumber Step 来创建对象时：

```gherkin
存在"购物车"：
| customer | products[0](商品).name | products[0].stocks[0](库存).size | products[0].stocks[0].count |
| Tom      | book                 | A4                             | 100                         |
```

那么会将如下数据创建到数据库中：

```gherkin
carts
| id | customer |
| 1  | Tom      |
products
| id | name | cart_id |
| 1  | book | 1       |
product_stocks
| id | size | count | product_id |
| 1  | A4   | 100   | 1          |
```

值得注意的是，`products[0](商品).name` 这个列名其实是一个表达式。其含义为，将 `products` 这个列表属性的第一个元素对象用 `商品` 这个工厂来创建，并将其属性 `name` 设置为列值，即 `book`
。像这样的列名表达式都是通过 [JFactory](https://github.com/leeonky/jfactory/blob/master/README.md) 来实现的。

## 为已存在的数据添加一对一的关联数据

假如有下面的实体类，并且工厂类已经注册：

```java

@Entity
@Getter
@Setter
@Table(name = "orders")
public class Order {

    @Id
    private long id;
    private String customer;

    @OneToOne
    private Product product;
}

@Entity
@Getter
@Setter
@Table(name = "products")
public class Product {

    @Id
    private long id;
    private String name;

}

public class Orders {
    public static class 订单 extends Spec<Order> {
    }
}

public class Products {
    public static class 商品 extends Spec<Product> {
    }
}
```

并且已经通过下面的 Cucumber Step 创建了订单对象：

```gherkin
存在"订单"：
| customer |
| Tom      |
```

当用如下的 Cucumber Step 来创建对象时：

```gherkin
存在"订单.customer[Tom].product"的"商品"：
| name    |
| bicycle |
```

或者，当用如下的 Api 来创建对象时：

```java
jData.prepare("订单.customer[Tom].product","商品",new HashMap<String, Object>(){{
        put("name","bicycle");
        }});
```

那么会将如下数据创建到数据库中：

```gherkin
orders
| id | customer |
| 1  | Tom      |
products
| id      | name    | order_id |
| 1       | bicycle | 1        |
```

## 为已存在的数据添加多对多的关联数据

假如有下面的实体类，并且工厂类已经注册：

```java

@Entity
@Getter
@Setter
@Table(name = "carts")
public class Cart {

    @Id
    private long id;
    private String customer;

    @OneToMany
    private List<Product> products = new ArrayList<>();
}

@Entity
@Getter
@Setter
@Table(name = "products")
public class Product {

    @Id
    private long id;
    private String name;

}
```

并且已经通过下面的 Cucumber Step 创建了购物车对象。请注意，这里除了创建购物车数据外，也同时创建了一条name为book的商品数据。

```gherkin
存在"购物车"：
| customer | products[0](商品).name |
| Tom      | book                 |
```

当用如下的 Cucumber Step 来创建对象时：

```gherkin
存在"购物车.customer[Tom].products"的"商品"：
| name    |
| bicycle |
```

那么会将如下数据创建到数据库中：

```gherkin
carts
| id | customer |
| 1  | Tom      |
products
| id      | name    | cart_id |
| 1       | book    | 1       |
| 2       | bicycle | 1       |
```

## 为已存在的数据添加默认关联数据

假如有下面的实体类，并且工厂类已经注册：

```java

@Entity
@Getter
@Setter
@Table(name = "carts")
public class Cart {

    @Id
    private long id;
    private String customer;

    @OneToMany
    private List<Product> products = new ArrayList<>();
}

@Entity
@Getter
@Setter
@Table(name = "products")
public class Product {

    @Id
    private long id;
    private String name;

}
```

并且已经通过下面的 Cucumber Step 创建了购物车对象。请注意，这里除了创建购物车数据外，也同时创建了一条name为book的商品数据。

```gherkin
存在"购物车"：
| customer | products[0](商品).name |
| Tom      | book                 |
```

当用如下的 Cucumber Step 来创建对象时：

```gherkin
存在"购物车.customer[Tom].products"的1个"商品"
```

那么会将如下数据创建到数据库中：

```gherkin
carts
| id | customer |
| 1  | Tom      |
products
| id | name   | cart_id |
| 1  | book   | 1       |
| 2  | name#1 | 1       |
```

## 为已存在的数据添加反向一对多的关联数据

假如有下面的实体类，并且工厂类已经注册：

```java

@Entity
@Getter
@Setter
@Table(name = "products")
public class Product {

    @Id
    private long id;
    private String name;

    @OneToMany(mappedBy = "product")
    private List<ProductStock> stocks = new ArrayList<>();

}

@Entity
@Getter
@Setter
@Table(name = "product_stocks")
public class ProductStock {

    @Id
    private long id;
    private String size;
    private int count;

    @ManyToOne
    private Product product;

}
```

并且已经通过下面的 Cucumber Step 创建了商品对象。

```gherkin
存在"商品"：
| name |
| book |
```

当用如下的 Cucumber Step 来创建对象时：

```gherkin
存在如下"库存"，并且其"product"为"商品.name[book]"：
| size | count |
| A3   | 10    |
```

那么会将如下数据创建到数据库中：

```gherkin
products
| id   | name |
| 1    | book |
product_stocks
| id   | size  | count | product_id |
| 1    | A3    | 10    | 1          |
```

## 为已存在的数据添加反向一对多的默认关联数据

假如有下面的实体类，并且工厂类已经注册：

```java

@Entity
@Getter
@Setter
@Table(name = "products")
public class Product {

    @Id
    private long id;
    private String name;

    @OneToMany(mappedBy = "product")
    private List<ProductStock> stocks = new ArrayList<>();

}

@Entity
@Getter
@Setter
@Table(name = "product_stocks")
public class ProductStock {

    @Id
    private long id;
    private String size;
    private int count;

    @ManyToOne
    private Product product;

}
```

并且已经通过下面的 Cucumber Step 创建了商品对象。

```gherkin
存在"商品"：
| name |
| book |
```

当用如下的 Cucumber Step 来创建对象时：

```gherkin
存在1个"库存"，并且其"product"为"商品.name[book]"
```

那么会将如下数据创建到数据库中：

```gherkin
products
| id   | name |
| 1    | book |
product_stocks
| id | size   | count | product_id |
| 1  | size#1 | 1     | 1          |
```
