Feature: Transactions CRUD operations

  Scenario: Getting all transactions
    Given The endpoint for "transactions" is available for method "GET"
    When I retrieve all transactions
    Then I should receive all transactions

#  Scenario: Create a transaction
#    Given The endpoint for "cars" is available for method "POST"
#    When I create a car with brand "Toyota" and license plate "CD4567" with weight 1600 and owner id 1
#    Then The response status is 201
#    And The car ID is 2