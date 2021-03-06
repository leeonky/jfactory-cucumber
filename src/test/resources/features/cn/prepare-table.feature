# language: zh-CN
功能: 用表格准备数据

  场景: 用表格准备数据
    假如存在"商品"：
      | name |
      | book |
    那么所有"商品"应为：
    """
      .size=1 and [0].name='book'
    """

  场景: 准备多层购物车数据
    假如存在"购物车"：
      | customer | products[0](商品).name | products[0].stocks[0](库存).size | products[0].stocks[0].count |
      | Tom      | book                 | A4                             | 100                         |
    那么"购物车"应为：
    """
      .customer='Tom'
      and .products.size=1
      and .products[0].name='book'
      and .products[0].stocks.size=1
      and .products[0].stocks[0].size='A4'
      and .products[0].stocks[0].count=100
    """

  场景: 转置表格准备数据
    假如存在"商品"：
      | 'name | book | bicycle |
      | color | red  | white   |
    那么所有"商品"应为：
    """
      .size=2
      and [0].name='book' and [0].color='red'
      and [1].name='bicycle' and [1].color='white'
    """

  场景: 为已有数据添加多对多关联数据
    假如存在"购物车"：
      | customer | products[0](商品).name |
      | Tom      | book                 |
    并且存在"购物车.customer[Tom].products"的"商品"：
      | name    |
      | bicycle |
    那么"购物车"应为：
    """
      .customer='Tom'
      and .products.size=2
      and .products[0].name='book'
      and .products[1].name='bicycle'
    """

  场景: 为已有数据添加一对一关联数据
    假如存在"订单"：
      | customer |
      | Tom      |
    并且存在"订单.customer[Tom].product"的"商品"：
      | name    |
      | bicycle |
    那么"订单"应为：
    """
      .customer='Tom'
      and .product.name='bicycle'
    """

  场景: 为已有数据添加默认关联数据
    假如存在"购物车"：
      | customer | products[0](商品).name |
      | Tom      | book                 |
    并且存在"购物车.customer[Tom].products"的1个"商品"
    那么"购物车"应为：
    """
      .customer='Tom'
      and .products.size=2
    """

  场景: 为已有数据添加反向一对多关联数据
    假如存在"商品"：
      | name |
      | book |
    并且存在如下"库存"，并且其"product"为"商品.name[book]"：
      | size | count |
      | A3   | 10    |
    那么"商品"应为：
    """
      .stocks.size=1
      and .stocks[0].size='A3'
      and .stocks[0].count=10
    """

  场景: 为已有数据添加反向一对多默认关联数据
    假如存在"商品"：
      | name |
      | book |
    并且存在1个"库存"，并且其"product"为"商品.name[book]"
    那么"商品"应为：
    """
      .stocks.size=1
    """
