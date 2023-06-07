Feature: Transactions CRUD operations

  Scenario: Getting all transactions
    Given The endpoint for "transactions" is available for method "GET"
    When I retrieve all transactions
    Then I should receive all transactions

  Scenario: Add withdraw transaction with valid data from payment account
    Given a user with username "john" and type "USER"
    And "john" has a "payment" account with IBAN "1234567890" and balance 1000 and transaction limit 500 and daily limit 500 and absolute limit 100
    When a withdraw transaction of 500 is added to the account
    Then the transaction is saved successfully

  Scenario: Add withdraw transaction with valid data from savings account
    Given a user with username "john" and type "USER"
    And "john" has a "savings" account with IBAN "1234567890" and balance 1000 and transaction limit 500 and daily limit 500 and absolute limit 100
    When a withdraw transaction of 500 is added to the account
    Then the transaction is saved successfully

  Scenario: Add deposit transaction with valid data from payment account
    Given a user with username "john" and type "USER"
    And "john" has a "payment" account with IBAN "1234567890" and balance 1000 and transaction limit 500 and daily limit 500 and absolute limit 100
    When a deposit transaction of 500 is added to the account
    Then the transaction is saved successfully

  Scenario: Add deposit transaction with valid data from savings account
    Given a user with username "john" and type "USER"
    And "john" has a "savings" account with IBAN "1234567890" and balance 1000 and transaction limit 500 and daily limit 500 and absolute limit 100
    When a deposit transaction of 500 is added to the account
    Then the transaction is saved successfully

  Scenario: Add deposit transaction without toAccountIban
    Given a user with username "john" and type "USER"
    And "john" has a "savings" account with IBAN "1234567890" and balance 1000 and transaction limit 500 and daily limit 500 and absolute limit 100
    When a deposit transaction of 500 is added to the account without a toAccountIban
    Then a RuntimeException is thrown with message "The to account can't be empty."

  Scenario: Add transfer transaction with valid data from payment account to payment account
    Given a user with username "john" and type "USER"
    And "john" has a "payment" account with IBAN "1234567890" and balance 1000 and transaction limit 500 and daily limit 500 and absolute limit 100
    And a "payment" account with IBAN "1234567891" and balance 1000 and transaction limit 500 and daily limit 500 and absolute limit 100
    When a transfer transaction of 500 to IBAN "1234567891" is added to the account
    Then the transaction is saved successfully

  Scenario: Add transfer transaction with valid data from savings account to payment account
    Given a user with username "john" and type "USER"
    And "john" has a "savings" account with IBAN "1234567890" and balance 1000 and transaction limit 500 and daily limit 500 and absolute limit 100
    And a "payment" account with IBAN "1234567891" and balance 1000 and transaction limit 500 and daily limit 500 and absolute limit 100
    When a transfer transaction of 500 to IBAN "1234567891" is added to the account
    Then the transaction is saved successfully

  Scenario: Add transfer transaction with valid data from savings account to savings account
    Given a user with username "john" and type "USER"
    And "john" has a "savings" account with IBAN "1234567890" and balance 1000 and transaction limit 500 and daily limit 500 and absolute limit 100
    And a "savings" account with IBAN "1234567891" and balance 1000 and transaction limit 500 and daily limit 500 and absolute limit 100
    When a transfer transaction of 500 to IBAN "1234567891" is added to the account
    Then the transaction is saved successfully

  Scenario: Add transfer transaction with valid data from payment account to savings account
    Given a user with username "john" and type "USER"
    And "john" has a "payment" account with IBAN "1234567890" and balance 1000 and transaction limit 500 and daily limit 500 and absolute limit 100
    And a "savings" account with IBAN "1234567891" and balance 1000 and transaction limit 500 and daily limit 500 and absolute limit 100
    When a transfer transaction of 500 to IBAN "1234567891" is added to the account
    Then the transaction is saved successfully

  Scenario: Add withdraw transaction with negative amount
    Given a user with username "john" and type "USER"
    And "john" has a "payment" account with IBAN "1234567890" and balance 1000 and transaction limit 500 and daily limit 500 and absolute limit 100
    When a withdraw transaction of -500 is added to the account
    Then a RuntimeException is thrown with message "The transaction amount can not be negative."

  Scenario: Add withdraw transaction with zero amount
    Given a user with username "john" and type "USER"
    And "john" has a "payment" account with IBAN "1234567890" and balance 1000 and transaction limit 500 and daily limit 500 and absolute limit 100
    When a withdraw transaction of 0 is added to the account
    Then a RuntimeException is thrown with message "The transaction amount can not be zero."

  Scenario: Add withdraw transaction with insufficient balance
    Given a user with username "john" and type "USER"
    And "john" has a "payment" account with IBAN "1234567890" and balance 1000 and transaction limit 500 and daily limit 500 and absolute limit 100
    When a withdraw transaction of 1500 is added to the account
    Then a RuntimeException is thrown with message "This transaction exceeds the absolute limit of this account."

  Scenario: Add transfer transaction without toAccountIban
    Given a user with username "john" and type "USER"
    And "john" has a "payment" account with IBAN "1234567890" and balance 1000 and transaction limit 500 and daily limit 500 and absolute limit 100
    When a transfer transaction of 500 is added to the account without a toAccountIban
    Then a RuntimeException is thrown with message "The to or from account can't be empty."

  Scenario: Add transfer transaction without toAccountIban
    Given a user with username "john" and type "USER"
    And "john" has a "payment" account with IBAN "1234567890" and balance 1000 and transaction limit 500 and daily limit 500 and absolute limit 100
    When a transfer transaction of 500 is added to the account without a fromAccountIban
    Then a RuntimeException is thrown with message "The to or from account can't be empty."

  Scenario: Add withdraw transaction with exceeded transaction limit
    Given a user with username "john" and type "USER"
    And "john" has a "payment" account with IBAN "1234567890" and balance 2000 and transaction limit 500 and daily limit 500 and absolute limit 100
    When a withdraw transaction of 1000 is added to the account
    Then a RuntimeException is thrown with message "The transaction limit for this account has been exceeded."

  Scenario: Add transfer transaction with with exceeded transaction limit
    Given a user with username "john" and type "USER"
    And "john" has a "payment" account with IBAN "1234567890" and balance 1000 and transaction limit 500 and daily limit 500 and absolute limit 100
    And a "savings" account with IBAN "1234567891" and balance 1000 and transaction limit 400 and daily limit 500 and absolute limit 100
    When a transfer transaction of 500 to IBAN "1234567891" is added to the account
    Then a RuntimeException is thrown with message "The transaction limit for this account has been exceeded."

  Scenario: Add transaction with exceeded daily limit
    Given a user with username "john" and type "USER"
    And "john" has a "payment" account with IBAN "1234567890" and balance 1000 and transaction limit 500 and daily limit 500 and absolute limit 100
    And a withdraw transaction of 400 is added to the account
    When another withdraw transaction of 200 is added to the account
    Then a other RuntimeException is thrown with message "This account exceeded the daily limit."

  Scenario: Add transaction with exceeded daily limit
    Given a user with username "john" and type "USER"
    And "john" has a "payment" account with IBAN "1234567890" and balance 1000 and transaction limit 500 and daily limit 500 and absolute limit 100
    And a "savings" account with IBAN "1234567891" and balance 1000 and transaction limit 500 and daily limit 500 and absolute limit 100
    And a transfer transaction of 500 to IBAN "1234567891" is added to the account
    When another transfer transaction of 200 to IBAN "1234567891" is added to the account
    Then a other RuntimeException is thrown with message "This account exceeded the daily limit."

  Scenario: Add transfer transaction with wrong iban
    Given a user with username "john" and type "USER"
    And another user with username "bert" and type "USER"
    And "john" has a "payment" account with IBAN "1234567890" and balance 1000 and transaction limit 500 and daily limit 500 and absolute limit 100
    And another user "bert" has a "payment" account with IBAN "1234567891" and balance 1000 and transaction limit 500 and daily limit 500 and absolute limit 100
    When a transfer transaction of 200 from IBAN "1234567891" to IBAN "1234567891" is added to the account
    Then a RuntimeException is thrown with message "This account does not belong to this user."

  Scenario: Add deposit transaction with wrong iban
    Given a user with username "john" and type "USER"
    And another user with username "bert" and type "USER"
    And "john" has a "payment" account with IBAN "1234567890" and balance 1000 and transaction limit 500 and daily limit 500 and absolute limit 100
    And another user "bert" has a "payment" account with IBAN "1234567891" and balance 1000 and transaction limit 500 and daily limit 500 and absolute limit 100
    When a deposit transaction of 200 to IBAN "1234567891" is added to the account
    Then a RuntimeException is thrown with message "This account does not belong to this user."

  Scenario: Add withdraw transaction with wrong iban
    Given a user with username "john" and type "USER"
    And another user with username "bert" and type "USER"
    And "john" has a "payment" account with IBAN "1234567890" and balance 1000 and transaction limit 500 and daily limit 500 and absolute limit 100
    And another user "bert" has a "payment" account with IBAN "1234567891" and balance 1000 and transaction limit 500 and daily limit 500 and absolute limit 100
    When a withdraw transaction of 200 from IBAN "1234567891" is added to the account
    Then a RuntimeException is thrown with message "This account does not belong to this user."