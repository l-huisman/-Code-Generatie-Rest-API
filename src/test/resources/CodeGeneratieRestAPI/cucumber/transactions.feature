Feature: Transactions CRUD operations

  Scenario: Getting all transactions
    Given The endpoint for "transactions" is available for method "GET"
    When I retrieve all transactions
    Then I should receive all transactions

  Scenario: Add transaction with valid data
    Given a user with username "john"
    And an account with IBAN "1234567890" and balance 1000
    When a transaction of 500 is added to the account
    Then the transaction is saved successfully

  Scenario: Add transaction with negative amount
    Given a user with username "john"
    And an account with IBAN "1234567890" and balance 1000
    When a transaction of -500 is added to the account
    Then a RuntimeException is thrown with message "The transaction amount can not be negative."

  Scenario: Add transaction with insufficient balance
    Given a user with username "john"
    And an account with IBAN "1234567890" and balance 1000
    When a transaction of 1500 is added to the account
    Then a RuntimeException is thrown with message "This account does not have enough balance to complete this transaction."

  Scenario: Add transaction to savings account with deposit
    Given a user with username "john"
    And a savings account with IBAN "1234567890" and balance 1000
    When a deposit transaction of 500 is added to the account
    Then a RuntimeException is thrown with message "A savings account can not be used for withdraws."

  Scenario: Add transaction to savings account with withdraw
    Given a user with username "john"
    And a savings account with IBAN "1234567890" and balance 1000
    When a withdraw transaction of 500 is added to the account
    Then the transaction is saved successfully

  Scenario: Add transfer transaction without toAccountId
    Given a user with username "john"
    And an account with IBAN "1234567890" and balance 1000
    When a transfer transaction of 500 is added to the account without a toAccountId
    Then a RuntimeException is thrown with message "A transfer transaction requires a toAccountId."

  Scenario: Add transaction with exceeded transaction limit
    Given a user with username "john"
    And an account with IBAN "1234567890" and balance 1000 and transaction limit 500
    When a transaction of 1000 is added to the account
    Then a RuntimeException is thrown with message "The daily limit for this account has been exceeded."

  Scenario: Add transaction with exceeded daily limit
    Given a user with username "john"
    And an account with IBAN "1234567890" and balance 1000 and daily limit 500
    And a transaction of 400 is added to the account
    When another transaction of 200 is added to the account
    Then a RuntimeException is thrown with message "This account exceeded the daily limit."