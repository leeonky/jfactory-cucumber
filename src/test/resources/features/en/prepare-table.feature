Feature: Prepare data with table

  Scenario: Prepare data with table
    Given Exists data "Product":
      | name |
      | book |
    Then All data "Product" should be:
    """
      .size=1 and [0].name='book'
    """

  Scenario: Prepare hierarchical data with table
    Given Exists data "Cart":
      | customer | products[0](Product).name | products[0].stocks[0](Inventory).size | products[0].stocks[0].count |
      | Tom      | book                      | A4                                    | 100                         |
    Then Data "Cart" should be:
    """
      .customer='Tom'
      and .products.size=1
      and .products[0].name='book'
      and .products[0].stocks.size=1
      and .products[0].stocks[0].size='A4'
      and .products[0].stocks[0].count=100
    """

  Scenario: Prepare data with transposed table
    Given Exists data "Product":
      | 'name | book | bicycle |
      | color | red  | white   |
    Then All data "Product" should be:
    """
      .size=2
      and [0].name='book' and [0].color='red'
      and [1].name='bicycle' and [1].color='white'
    """

  Scenario: Prepare data in many-to-many relation
    Given Exists data "Cart":
      | customer | products[0](Product).name |
      | Tom      | book                      |
    And Exists "Cart.customer[Tom].products" as data "Product":
      | name    |
      | bicycle |
    Then Data "Cart" should be:
    """
      .customer='Tom'
      and .products.size=2
      and .products[0].name='book'
      and .products[1].name='bicycle'
    """

  Scenario: Prepare data in one-to-one relation
    Given Exists data "Order":
      | customer |
      | Tom      |
    And Exists "Order.customer[Tom].product" as data "Product":
      | name    |
      | bicycle |
    Then Data "Order" should be:
    """
      .customer='Tom'
      and .product.name='bicycle'
    """

  Scenario: Prepare default data in one-to-one relation
    Given Exists data "Cart":
      | customer | products[0](Product).name |
      | Tom      | book                      |
    And Exists "Cart.customer[Tom].products" as 1 data "Product"
    Then Data "Cart" should be:
    """
      .customer='Tom'
      and .products.size=2
    """

  Scenario: Prepare one-to-many data for exists data
    Given Exists data "Product":
      | name |
      | book |
    And Exists following data "Inventory", and its "product" is "Product.name[book]":
      | size | count |
      | A3   | 10    |
    Then Data "Product" should be:
    """
      .stocks.size=1
      and .stocks[0].size='A3'
      and .stocks[0].count=10
    """

  Scenario: Prepare one-to-many default data for exists data
    Given Exists data "Product":
      | name |
      | book |
    And Exists 1 data "Inventory", and its "product" is "Product.name[book]"
    Then Data "Product" should be:
    """
      .stocks.size=1
    """
