Feature: Transactions CRUD operations

  Scenario: Getting all transactions
    Given I am logged in as "admin" with password "admin"
    And The endpoint for "transactions" is available for method "GET"
    When I retrieve all transactions
    Then I should receive all transactions

  Scenario: Add withdraw transaction with valid data from payment account
    Given I am logged in as "Devon" with password "devon"
    And The endpoint for "transactions" is available for method "POST"
    When I add a withdraw transaction of 80 from "NL61-INHO-0897-9124-94"
    Then The response status code is 200
    And The amount of the saved transaction is 80

  Scenario: Add withdraw transaction with valid data from savings account
    Given I am logged in as "Devon" with password "devon"
    And The endpoint for "transactions" is available for method "POST"
    When I add a withdraw transaction of 80 from "NL61-INHO-0897-9124-93"
    Then The response status code is 200
    And The amount of the saved transaction is 80

  Scenario: Add deposit transaction with valid data to savings account
    Given I am logged in as "Devon" with password "devon"
    And The endpoint for "transactions" is available for method "POST"
    When I add a deposit transaction of 80 to "NL61-INHO-0897-9124-93"
    Then The response status code is 200
    And The amount of the saved transaction is 80

  Scenario: Add deposit transaction with valid data to payment account
    Given I am logged in as "Devon" with password "devon"
    And The endpoint for "transactions" is available for method "POST"
    When I add a deposit transaction of 80 to "NL61-INHO-0897-9124-94"
    Then The response status code is 200
    And The amount of the saved transaction is 80

  Scenario: Add transfer transaction with valid data from savings to other account
    Given I am logged in as "Devon" with password "devon"
    And The endpoint for "transactions" is available for method "POST"
    When I add a transfer transaction of 80 from "NL61-INHO-0897-9124-93" to "NL61-INHO-0897-9124-94"
    Then The response status code is 200
    And The amount of the saved transaction is 80

  Scenario: Add transfer transaction with valid data from other to savings account
    Given I am logged in as "Devon" with password "devon"
    And The endpoint for "transactions" is available for method "POST"
    When I add a transfer transaction of 80 from "NL61-INHO-0897-9124-94" to "NL61-INHO-0897-9124-93"
    Then The response status code is 200
    And The amount of the saved transaction is 80

  Scenario: Add deposit transaction without toAccountIban
    Given I am logged in as "Devon" with password "devon"
    And The endpoint for "transactions" is available for method "POST"
    When I add a deposit transaction of 80 without a toAccountIban
    Then The response status code is 400
    And The message returned is "The to account can't be empty."

  Scenario: Add withdraw transaction without fromAccountIban
    Given I am logged in as "Devon" with password "devon"
    And The endpoint for "transactions" is available for method "POST"
    When I add a withdraw transaction of 80 without a fromAccountIban
    Then The response status code is 400
    And The message returned is "The from account can't be empty."

  Scenario: Add transfer transaction from own savings to other not own account
    Given I am logged in as "Devon" with password "devon"
    And The endpoint for "transactions" is available for method "POST"
    When I add a transfer transaction of 80 from "NL61-INHO-0897-9124-93" to "NL61-INHO-0897-9124-92"
    Then The response status code is 400
    And The message returned is "It is not possible to transfer from a savings account to an account that is not your account."

  Scenario: Add transfer transaction from own payment to other not own savings
    Given I am logged in as "Devon" with password "devon"
    And The endpoint for "transactions" is available for method "POST"
    When I add a transfer transaction of 80 from "NL61-INHO-0897-9124-94" to "NL61-INHO-0897-9124-90"
    Then The response status code is 400
    And The message returned is "It is not possible to transfer to a savings account from an account that is not your account."

  Scenario: Add withdraw transaction with negative amount
    Given I am logged in as "Devon" with password "devon"
    And The endpoint for "transactions" is available for method "POST"
    When I add a withdraw transaction of -10 from "NL61-INHO-0897-9124-93"
    Then The response status code is 400
    And The message returned is "The transaction amount can't be negative."

  Scenario: Add withdraw transaction with negative amount
    Given I am logged in as "Devon" with password "devon"
    And The endpoint for "transactions" is available for method "POST"
    When I add a withdraw transaction of 0 from "NL61-INHO-0897-9124-93"
    Then The response status code is 400
    And The message returned is "The transaction amount can't be zero."

  Scenario: Add transfer transaction without toAccountIban
    Given I am logged in as "Devon" with password "devon"
    And The endpoint for "transactions" is available for method "POST"
    When I add a transfer transaction of 80 from "NL61-INHO-0897-9124-94" to no iban
    Then The response status code is 400
    And The message returned is "The to or from account can't be empty."

  Scenario: Add transfer transaction without fromAccountIban
    Given I am logged in as "Devon" with password "devon"
    And The endpoint for "transactions" is available for method "POST"
    When I add a transfer transaction of 80 from no iban to "NL61-INHO-0897-9124-93"
    Then The response status code is 400
    And The message returned is "The to or from account can't be empty."

  Scenario: Add withdraw transaction with exceeded transaction limit
    Given I am logged in as "Devon" with password "devon"
    And The endpoint for "transactions" is available for method "POST"
    When I add a withdraw transaction of 120 from "NL61-INHO-0897-9124-93"
    Then The response status code is 400
    And The message returned is "The transaction limit for this account has been exceeded."

  Scenario: Add withdraw transaction with exceeded daily limit
    Given I am logged in as "Devon" with password "devon"
    And The endpoint for "transactions" is available for method "POST"
    When I add a withdraw transaction of 90 from "NL61-INHO-0897-9124-95"
    And I add a withdraw transaction of 90 from "NL61-INHO-0897-9124-95"
    And I add a withdraw transaction of 90 from "NL61-INHO-0897-9124-95"
    Then The response status code is 400
    And The message returned is "This account exceeded the daily limit."

  Scenario: Add transfer transaction exceeded transaction limit
    Given I am logged in as "Devon" with password "devon"
    And The endpoint for "transactions" is available for method "POST"
    When I add a withdraw transaction of 120 from "NL61-INHO-0897-9124-95"
    Then The response status code is 400
    And The message returned is "The transaction limit for this account has been exceeded."

  Scenario: Add transfer transaction exceeded daily limit
    Given I am logged in as "Devon" with password "devon"
    And The endpoint for "transactions" is available for method "POST"
    When I add a transfer transaction of 90 from "NL61-INHO-0897-9124-95" to "NL61-INHO-0897-9124-93"
    And I add a transfer transaction of 90 from "NL61-INHO-0897-9124-95" to "NL61-INHO-0897-9124-93"
    And I add a transfer transaction of 90 from "NL61-INHO-0897-9124-95" to "NL61-INHO-0897-9124-93"
    Then The response status code is 400
    And The message returned is "This account exceeded the daily limit."

  Scenario: Add transfer transaction not owning the from account
    Given I am logged in as "Devon" with password "devon"
    And The endpoint for "transactions" is available for method "POST"
    When I add a transfer transaction of 90 from "NL61-INHO-0897-9124-91" to "NL61-INHO-0897-9124-93"
    Then The response status code is 400
    And The message returned is "This account does not belong to this user."

  Scenario: Add transfer transaction not owning the from account
    Given I am logged in as "Devon" with password "devon"
    And The endpoint for "transactions" is available for method "POST"
    When I add a transfer transaction of 90 from "NL61-INHO-0897-9124-91" to "NL61-INHO-0897-9124-93"
    Then The response status code is 400
    And The message returned is "This account does not belong to this user."

  Scenario: Add withdraw transaction not owning the from account
    Given I am logged in as "Devon" with password "devon"
    And The endpoint for "transactions" is available for method "POST"
    When I add a withdraw transaction of 90 from "NL61-INHO-0897-9124-91"
    Then The response status code is 400
    And The message returned is "This account does not belong to this user."

  Scenario: Add deposit transaction not owning the from account
    Given I am logged in as "Devon" with password "devon"
    And The endpoint for "transactions" is available for method "POST"
    When I add a deposit transaction of 90 to "NL61-INHO-0897-9124-91"
    Then The response status code is 400
    And The message returned is "This account does not belong to this user."
