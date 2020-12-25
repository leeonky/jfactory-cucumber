# language: zh-CN
功能: 准备数据

  场景: 创建订单
    假如存在"订单"：
      | customer |
      | James    |
    那么数据"订单"应为：
    """
      ["** .customer='James'"]
    """
