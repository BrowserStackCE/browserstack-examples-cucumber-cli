Feature: Product Feature

  Scenario: Apply Lowest to Highest Order By
    Given I navigate to website
    And I order by lowest to highest
    Then I should see prices in ascending order