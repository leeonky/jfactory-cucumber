# language: zh-CN
功能: 用DAL准备数据

  场景: 准备商品-DAL格式
    假如存在"商品"：
    """
    name: 'book'.toUpperCase
    color: red
    """
    那么所有"商品"应为：
    """
    :  | name | color |
       | BOOK | red   |
    """

  场景: 准备多个商品-DAL格式
    假如存在"商品"：
    """
    | name | color |
    | book | red   |
    | iPad | white |
    """
    那么所有"商品"应为：
    """
    :  | name | color |
       | book | red   |
       | iPad | white |
    """

  场景: 准备商品-all in one: single object
    假如存在：
    """
    商品: {
      name: 'book'.toUpperCase
      color: red
    }
    """
    那么所有"商品"应为：
    """
    :  | name | color |
       | BOOK | red   |
    """

  场景: 准备商品-all in one: list
    假如存在：
    """
    商品: | name | color |
          | BOOK | red   |
    """
    那么所有"商品"应为：
    """
    :  | name | color |
       | BOOK | red   |
    """

  场景: 准备商品-all in one with out properties
    假如存在：
    """
    商品
    商品
    商品
    """
    那么所有"商品"应为：
    """
    ::size= 3
    """
