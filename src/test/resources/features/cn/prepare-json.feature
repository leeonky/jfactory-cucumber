# language: zh-CN
功能: 用JSON准备数据

  场景: 准备商品-JSON数组格式
    假如存在"商品"：
    """
      [{
        "name": "book",
      },{
        "name": "bicycle"
      }]
    """
    那么所有"商品"数据应为：
    """
      .size=2
      and [0].name='book'
      and [1].name='bicycle'
    """

  场景: 准备商品-JSON格式
    假如存在"商品"：
    """
      {
        "name": "book"
      }
    """
    那么所有"商品"数据应为：
    """
      .size=1
      and [0].name='book'
    """
