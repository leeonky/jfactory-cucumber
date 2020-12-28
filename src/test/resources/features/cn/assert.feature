# language: zh-CN
功能: 数据检查

  场景: 获取并检查数据
    假如存在"订单"：
    """
      customer: James
      merchant: JD
    """
    那么所有"订单.customer[James]"数据应为：
    """
      .size=1
      and [0].customer='James'
    """
    那么"订单.customer[James]"数据应为：
    """
      .customer='James'
    """

