# language: zh-CN
功能: 用JSON准备数据

  场景: 准备订单-JSON数组格式
    假如存在"订单"：
    """
      [{
        "customer": "James",
      },{
        "customer": "Tomas"
      }]
    """
    那么所有"订单"数据应为：
    """
      .size=2
      and [0].customer='James'
      and [1].customer='Tomas'
    """

  场景: 准备订单-JSON格式
    假如存在"订单"：
    """
      {
        "customer": "James"
      }
    """
    那么所有"订单"数据应为：
    """
      .size=1
      and [0].customer='James'
    """
