# language: zh-CN
功能: 数据检查

  场景: 获取并检查数据
    假如存在"商品"：
    """
      name: book
    """
    那么所有"商品.name[book]"应为：
    """
      .size=1
      and [0].name='book'
    """
    那么"商品.name[book]"应为：
    """
      .name='book'
    """

  场景: 获取并检查数据-英文标点
    假如存在"商品":
    """
      name: book
    """
    那么所有"商品.name[book]"应为:
    """
      .size=1
      and [0].name='book'
    """
    那么"商品.name[book]"应为:
    """
      .name='book'
    """

