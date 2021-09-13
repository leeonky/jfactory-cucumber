# 准备默认数据

准备默认数据是指用一些已通过 `Spec` 注册过的工厂类，在不设置任何属性的情况下，创建出一个或者多个对象。

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
        @Trait
        public 商品 红色的() {
            property("color").value("red");
            return this;
        }
    }
}
```

当用如下的 Cucumber Step 来创建对象时：

```gherkin
存在1个"红色的 商品"
```

或者，当用如下的 Api 来创建对象时：

```java
jData.prepare(1,"红色的 商品");
```

那么都会将如下数据创建到数据库中：

```gherkin
| id | name   | color |
| 1  | name#1 | red   |
```

值得注意的是，对于其他未显式设置值的属性（如id和name），会被设置默认值。具体默认值的规则，由 [JFactory](https://github.com/leeonky/jfactory/blob/master/README.md)
决定。