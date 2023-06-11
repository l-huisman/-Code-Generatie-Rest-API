Feature: Accounts CRUD operations

  Scenario: Getting all accounts
    Given I am logged in as "admin" with password "admin"
    And The endpoint for "accounts" is available for method "GET"
    When I retrieve all accounts
    Then I should receive all accounts
