# language: zh-CN
功能: 用YAML准备数据

  场景: 准备商品-YAML数组格式
    假如存在"商品"：
    """
      - name: book
        color: red
      - name: bicycle
        color: white
    """
    那么所有"商品"应为：
    """
      .size=2
      and [0].name='book' and [0].color='red'
      and [1].name='bicycle' and [1].color='white'
    """

  场景: 准备商品-YAML格式
    假如存在"商品"：
    """
      name: book
      color: red
    """
    那么所有"商品"应为：
    """
      .size=1
      and [0].name='book' and [0].color='red'
    """

  场景: 准备多层购物车数据
    假如存在"购物车"：
    """
    customer: Tom
    products:
    - _: (商品)
      name: book
      stocks:
      - _: (库存)
        size: A4
        count: '100'
    """
#    same with:
#    | customer | products[0](商品).name | products[0].stocks[0](库存).size | products[0].stocks[0].count |
#    | Tom      | book                 | A4                             | 100                         |
    那么"购物车"应为：
    """
      .customer='Tom'
      and .products.size=1
      and .products[0].name='book'
      and .products[0].stocks.size=1
      and .products[0].stocks[0].size='A4'
      and .products[0].stocks[0].count=100
    """
