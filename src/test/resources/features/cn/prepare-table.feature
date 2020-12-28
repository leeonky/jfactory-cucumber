# language: zh-CN
功能: 用表格准备数据

  场景: 用表格准备数据
    假如存在"订单"：
      | customer |
      | James    |
    那么所有"订单"数据应为：
    """
      .size=1 and [0].customer='James'
    """
