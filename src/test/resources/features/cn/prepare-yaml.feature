# language: zh-CN
功能: 用YAML准备数据

  场景: 准备订单-YAML数组格式
    假如存在"订单"：
    """
      - customer: James
        merchant: JD
      - customer: Tomas
        merchant: Taobao
    """
    那么所有"订单"数据应为：
    """
      .size=2
      and [0].customer='James' and [0].merchant='JD'
      and [1].customer='Tomas' and [1].merchant='Taobao'
    """

  场景: 准备订单-YAML格式
    假如存在"订单"：
    """
      customer: James
      merchant: JD
    """
    那么所有"订单"数据应为：
    """
      .size=1
      and [0].customer='James' and [0].merchant='JD'
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
