Feature: Data check

  Scenario: Get and check data
    Given Exists data "Product":
    """
      name: book
    """
    Then All data "Product.name[book]" should be:
    """
      .size=1
      and [0].name='book'
    """
    And Data "Product.name[book]" should be:
    """
      .name='book'
    """
