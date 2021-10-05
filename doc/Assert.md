# 使用富文本方式来验证数据

JFactor-Cucumber 支持用 Cucumber 提供的富文本机制，基于通过 `Spec` 注册过的工厂类，验证数据库中的数据。

## 验证所有数据

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

并且在数据库中已经存在如下的商品数据：

```gherkin
products
| id   | name |
| 1    | book |
```

那么如下的 Cucumber Step 验证数据的结果是通过的。

```gherkin
所有"商品.name[book]"应为：
"""
  .size=1
  and [0].name='book'
"""
```

值得注意的是，`商品.name[book]` 其实是一个表达式。其含义为，在 `商品` 这个工厂对应的数据中查询出所有 `name` 属性为 `book`
的数据。像这样的表达式都是通过 [JFactory](https://github.com/leeonky/jfactory/blob/master/README.md) 来实现的。 另外，`.size=1`
这个表达式的含义是指所有符合条件的数据一共为1个，`and [0].name='book'` 则是说第一个元素的 `name` 属性为 `book`
。像这样的表达式都是通过[DAL-java](https://github.com/leeonky/DAL-java/blob/master/README.md) 来实现的。

与上面的 Cucumber Step 类似，如下所示的 Step 验证数据的结果也是通过的。

```gherkin
"商品.name[book]"应为：
"""
 .name='book'
"""
```

