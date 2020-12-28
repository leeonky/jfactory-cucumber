# language: zh-CN
功能: 用表格准备数据

  场景: 用表格准备数据
    假如存在"商品"：
      | name |
      | book |
    那么所有"商品"数据应为：
    """
      .size=1 and [0].name='book'
    """
