# 使用 Json 方式来准备数据

除了用表格的方式之外，还可以用 Json 来准备数据。与表格类似，这种方式指的是用一些已通过 `Spec` 注册过的工厂类，利用 Cucumber 提供的富文本机制，创建出一个或者多个对象。

## 用 Json 对象准备数据

假如有下面的实体类，并且工厂类已经注册：

```java

@Entity
@Getter
@Setter
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
"""
  {
    "name": "book"
  }
"""
```

那么都将如下数据创建到数据库中：

```gherkin
products
| id | name | color   |
| 1  | book | color#1 |
```

值得注意的是，对于其他未显式设置值的属性（如id和color），会被设置默认值。具体默认值的规则，由 [JFactory](https://github.com/leeonky/jfactory/blob/master/README.md)
决定。

## 用 Json 数组对象准备数据

假如有下面的实体类，并且工厂类已经注册：

```java

@Entity
@Getter
@Setter
public class Product {
    @Id
    private long id;
    private String name;
}

public class Products {
    public static class 商品 extends Spec<Product> {
    }
}
```

当用如下的 Cucumber Step 来创建对象时：

```gherkin
存在"商品"：
"""
  [{
    "name": "book",
  },{
    "name": "bicycle"
  }]
"""
```

那么都将如下数据创建到数据库中：

```gherkin
products
| id | name    |
| 1  | book    |
| 2  | bicycle |
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
"""
    {
      "customer": "Tom",
      "products": [{
        "_": "(商品)",
        "name": "book",
        "stocks": [{
          "_": "(库存)",
          "size": "A4",
          "count": "100"
        }]
       }]
    }
"""
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

值得注意的是，`"_": "(商品)"` 这个 Json 属性其实是一个表达式。其含义为，将当前这个 Json 对象用 `商品`
这个工厂来创建。像这样的列名表达式都是通过 [JFactory](https://github.com/leeonky/jfactory/blob/master/README.md) 来实现的。

