Feature: Accounts CRUD operations

  Scenario: Getting all accounts
    Given I am logged in as "admin" with password "admin" to do some account stuff
    And The endpoint for "accounts" is available for method "GET" to do some account stuff
    When I retrieve all accounts
    Then I should receive all accounts

  Scenario: Getting an account
    Given I am logged in as "admin" with password "admin" to do some account stuff
    And The endpoint for "accounts" is available for method "GET" to do some account stuff
    When I retrieve account with IBAN "NL61-INHO-0897-9124-95"
    Then I should receive account with IBAN "NL61-INHO-0897-9124-95"
#  Scenario: Creating an account
#    Given I am logged in as "admin" with password "admin" to do some account stuff
#    And The endpoint for "accounts" is available for method "POST" to do some account stuff
#    When I create an account with  and balance "1000"
#    Then I should receive account with IBAN "NL61-INHO-0897-9124-95" and balance "1000"