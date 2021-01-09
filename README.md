# JFactory-Cucumber

[![travis-ci](https://travis-ci.org/leeonky/jfactory-cucumber.svg?branch=master)](https://travis-ci.org/leeonky/jfactory-cucumber)
[![coveralls](https://img.shields.io/coveralls/github/leeonky/jfactory-cucumber/master.svg)](https://coveralls.io/github/leeonky/jfactory-cucumber)
[![Mutation testing badge](https://img.shields.io/endpoint?style=flat&url=https%3A%2F%2Fbadge-api.stryker-mutator.io%2Fgithub.com%2Fleeonky%2Fjfactory-cucumber%2Fmaster)](https://dashboard.stryker-mutator.io/reports/github.com/leeonky/jfactory-cucumber/master)
[![Lost commit](https://img.shields.io/github/last-commit/leeonky/jfactory-cucumber.svg)](https://github.com/leeonky/jfactory-cucumber)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.leeonky/jfactory-cucumber.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.leeonky/jfactory-cucumber)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/db8d621e325c41a4bce126652ce6defa)](https://www.codacy.com/gh/leeonky/jfactory-cucumber/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=leeonky/jfactory-cucumber&amp;utm_campaign=Badge_Grade)
[![Maintainability](https://api.codeclimate.com/v1/badges/b1c9cde0a7682bd83821/maintainability)](https://codeclimate.com/github/leeonky/jfactory-cucumber/maintainability)
[![Code Climate issues](https://img.shields.io/codeclimate/issues/leeonky/jfactory-cucumber.svg)](https://codeclimate.com/github/leeonky/jfactory-cucumber/maintainability)
[![Code Climate maintainability (percentage)](https://img.shields.io/codeclimate/maintainability-percentage/leeonky/jfactory-cucumber.svg)](https://codeclimate.com/github/leeonky/jfactory-cucumber/maintainability)

---

基于[JFactory](https://github.com/leeonky/jfactory)的测试工具库，为验收测试提供相对标准的数据准备和断言支持。

## 准备测试数据

测试代码可以通过注入JData后直接调用接口方法的方式准备测试数据，也可以通过内置的预定义Cucumber Step用数据表格来准备数据。

无论以哪种形式准备数据，JData都会通过一些字符串形式的表达式表明某些特定的语意，假如如下的JFactory Spec已经注册：
```java
public class Products {
    public static class 商品 extends Spec<Product> {
        @Override
        public void main() {
            property("stocks").reverseAssociation("product");
        }

        @Trait
        public 商品 红色的() {
            property("color").value("red");
            return this;
        }
    }
}
```
通常会用到如下的几个表达式参数：
- traitsSpec 要准备数据的Trait和Spec，比如可以用如下代码构造一个商品集合

```java
jData.prepare("红色的 商品", new HashMap<String, String>() {{
    put("name", "book");
}});
```

- queryExpression 对象检索表达式，基本形式为"Spec.property-chain[value]"，表示对应Spec所指类型的所有对象中，返回属性为value的那些对象，
如下的表达式可以解释为检索出name为book的"商品"。

```java
List<Product> products = jData.queryAll("商品.name[book]");
Product product = jData.query("商品.name[book]");
```

- beanProperty 对象属性标识，该标识由两部分组成，其形式可以理解为：queryExpression.property，前半部分为对象检索表达式，表示要针对哪个对象，
property为属性名。该标识主要用于为已存在的对象关联子对象。

### API接口

- 给定部分属性值，准备对象
```java
<T> List<T> prepare(String traitsSpec, Table table)
<T> List<T> prepare(String traitsSpec, Map<String, ?>... data)
<T> List<T> prepare(String traitsSpec, List<Map<String, ?>> data)
```

- 以默认属性值，准备一定数量的对象
```java
<T> List<T> prepare(int count, String traitsSpec)
```

- 查询某些符合条件的数据
```java
<T> T query(String queryExpression)
<T> Collection<T> queryAll(String queryExpression)
```

- 为已存在的对象准备子对象数据
```java
<T> List<T> prepareAttachments(String beanProperty, String traitsSpec, List<Map<String, ?>> data)
```

- 为已存在的对象准备默认子对象数据
```java
<T> List<T> prepareAttachments(String beanProperty, int count, String traitsSpec)
```

- 为已存在的对象通过反向关联准备子对象数据
```java
<T> List<T> prepareAttachments(String traitsSpec, String reverseAssociationProperty, String queryExpression,
                                          List<Map<String, ?>> data)
```

- 为已存在的对象通过反向关联准备默认子对象数据
```java
<T> List<T> prepareAttachments(int count, String traitsSpec, String reverseAssociationProperty, String queryExpression)
```

### Cucumber Step
以上的API除检索数据接口外，都可以通过Cucumber Step的方式直接调用，加入有如下对象关系：
```java
//Entity
public class Cart {
    public String customer;
    public List<Product> products = new ArrayList<>();
}

public class Product {
    public String name;
    public String color;
    public List<ProductStock> stocks = new ArrayList<>();
}

public class ProductStock {
    public Product product;
    public String size;
    public int count;
}

public class Order {
    public String customer;
    public Product product;
}

//Spec
public static class 购物车 extends Spec<Cart> {
}

public static class 订单 extends Spec<Order> {
}

public static class 商品 extends Spec<Product> {
    @Override
    public void main() {
        property("stocks").reverseAssociation("product");
    }

    @Trait
    public 商品 红色的() {
        property("color").value("red");
        return this;
    }
}

public static class 库存 extends Spec<ProductStock> {
}

```


那么可以通过内建Step完成如下数据准备：

- 给定部分属性值，准备对象
```gherkin
    假如存在"商品"：
      | name |
      | book |
```

- 以默认属性值，准备一定数量的对象
```gherkin
    假如存在1个"红色的 商品"
```

- 为已存在的对象准备子对象数据
```gherkin
# 购物车 has many 商品 through Cart::products
    假如存在"购物车"：
      | customer | products[0](商品).name |
      | Tom      | book                 |
    并且存在"购物车.customer[Tom].products"的"商品"：
      | name    |
      | bicycle |

# 订单 has one 商品 through Order::product
    假如存在"订单"：
      | customer |
      | Tom      |
    并且存在"订单.customer[Tom].product"的"商品"：
      | name    |
      | bicycle |
```

- 为已存在的对象准备默认子对象数据
```gherkin
    假如存在"购物车"：
      | customer |
      | Tom      |
    并且存在"购物车.customer[Tom].products"的1个"商品"
```

- 为已存在的对象通过反向关联准备子对象数据
```gherkin
# 库存 belongs to 商品 through ProductStock::product
    假如存在"商品"：
      | name |
      | book |
    并且存在如下"库存"，并且其"product"为"商品.name[book]"：
      | size | count |
      | A3   | 10    |
```

- 为已存在的对象通过反向关联准备默认子对象数据
```gherkin
    假如存在"商品"：
      | name |
      | book |
    并且存在1个"库存"，并且其"product"为"商品.name[book]"
```

注意目前只支持中文环境

## 数据结构断言

一个多层对象尤其是Map或者JSON，用Java语言对其子属性进行测试断言是比较麻烦的。因此这里引入了一个专门断言对象数据的工具语言。
这个语言在解析执行时总是针对一个被断言对象，然后用"."运算符访问其子属性。用"[]"运算符访问集合元素。并且数据是没有类型的。

比如判断一个被测对象的name属性是"Tom"，age属性是19，可以如下表达：
```sql
.name='Tom' and .age=19
```

### API接口

- 检索一个对象并断言

```java
void should(String queryExpression, String dalExpression)
```

- 检索对象集合并断言

```java
void allShould(String queryExpression, String dalExpression)
```

### Cucumber Step

- 检索一个对象并断言
```gherkin
    那么"商品.name[book]"应为：
    """
      .name='book'
    """
```

- 检索对象集合并断言
```gherkin
    那么所有"商品"应为：
    """
      .size=1
      and [0].name='book'
    """
```
