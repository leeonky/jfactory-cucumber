Feature: bug

  Scenario: parent object must be queried after children prepared
    Given Exists data "Order":
      | product | customer |
      |         | tom      |
    When Exists "Order.customer[tom].product" as data "Product":
      | category.name |
      | PC            |
    Then Data "Order" should be:
    """
    : {
     customer= tom
     product.category.name= PC
    }
    """
