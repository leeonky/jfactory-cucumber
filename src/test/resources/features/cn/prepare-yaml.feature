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
    那么所有"商品"数据应为：
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
    那么所有"商品"数据应为：
    """
      .size=1
      and [0].name='book' and [0].color='red'
    """
