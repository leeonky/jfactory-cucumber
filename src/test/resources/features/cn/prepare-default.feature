# language: zh-CN
功能: 准备数据

  场景: 准备默认商品
    假如存在1个"红色的 商品"
    那么所有"商品"应为：
    """
      .size=1 and [0].color='red'
    """
