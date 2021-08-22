Feature: prepare data

  Scenario: prepare data with all default properties
    Given Exists 1 data "Red Product"
    Then All data "Product" should be:
    """
      .size=1 and [0].color='red'
    """
