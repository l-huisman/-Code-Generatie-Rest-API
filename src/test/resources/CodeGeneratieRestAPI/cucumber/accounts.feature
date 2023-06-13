Feature: Accounts CRUD operations

  Scenario: Getting all accounts
    Given I am logged in as "admin" with password "admin" to do some account stuff
    And The endpoint for "accounts" is available for method "GET" to do some account stuff
    When I retrieve all accounts
    Then I should receive all accounts

  Scenario: Getting an account by IBAN
    Given I am logged in as "admin" with password "admin" to do some account stuff
    And The endpoint for "accounts" is available for method "GET" to do some account stuff
    When I retrieve account with IBAN "NL61-INHO-0897-9124-95"
    Then I should receive account with IBAN "NL61-INHO-0897-9124-95"

  Scenario: Getting an account by IBAN that does not exist
    Given I am logged in as "admin" with password "admin" to do some account stuff
    And The endpoint for "accounts" is available for method "GET" to do some account stuff
    When I retrieve account with IBAN "NL61-AAAA-0897-9124-11"
    Then I should receive an error that the account does not exist

  Scenario: Creating an account
    Given I am logged in as "Dewi" with password "Dewi" to do some account stuff
    And The endpoint for "accounts" is available for method "POST" to do some account stuff
    When I create a new account
    Then I should receive the new account

  Scenario: Creating an account with invalid IBAN
    Given I am logged in as "Dewi" with password "Dewi" to do some account stuff
    And The endpoint for "accounts" is available for method "POST" to do some account stuff
    When I create a new account with IBAN "NL61-INHO-0897-9124-95"
    Then I should receive an error

  Scenario: Updating an account
    Given I am logged in as "Dewi" with password "Dewi" to do some account stuff
    And The endpoint for "accounts" is available for method "PUT" to do some account stuff
    When I update an account with IBAN "NL61-INHO-0897-9124-92"
    Then I should receive the updated account

  Scenario: Updating an account that I don't own
    Given I am logged in as "Dewi" with password "Dewi" to do some account stuff
    And The endpoint for "accounts" is available for method "PUT" to do some account stuff
    When I update an account with IBAN "NL61-INHO-0897-9124-95"
    Then I should receive an error that I don't own the account

#  Scenario: Deleting an account
#    Given I am logged in as "Dewi" with password "Dewi" to do some account stuff
#    And The endpoint for "accounts" is available for method "DELETE" to do some account stuff
#    When I delete an account with IBAN "NL61-INHO-0897-9124-90"
#    Then I should receive a message that the account is deleted