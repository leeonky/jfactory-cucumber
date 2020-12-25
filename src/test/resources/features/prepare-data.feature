# language: zh-CN
功能: 准备数据

  场景: 创建订单
    假如存在"订单"：
      | customer |
      | James    |
    那么所有"订单"数据应为：
    """
      .size=1 and [0].customer='James'
    """
