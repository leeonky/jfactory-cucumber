# 准备默认数据

准备默认数据是指用一些已通过 `Spec` 注册过的工厂类，在不设置任何属性的情况下，创建出一个或者多个对象。

假如下面的工厂类已经注册：

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

当用如下的 Cucumber Step 来创建对象时：

```gherkin
存在1个"红色的 商品"
```

或者，当用如下的 Api 来创建对象时：

```java
jData.prepare(1,"红色的 商品");
```

那么都将得到如下的结果：

```gherkin
所有"商品"应为：
"""
  .size=1 and [0].color='red'
"""
```