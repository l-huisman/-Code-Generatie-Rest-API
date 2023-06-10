Feature: Accounts CRUD operations
  Scenario: Getting all accounts
    Given The endpoint for "accounts" is available
    When I retrieve all accounts
    Then I should retrieve all accounts