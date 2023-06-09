package com.example.CodeGeneratieRestAPI.services;

import com.example.CodeGeneratieRestAPI.dtos.TransactionRequestDTO;
import com.example.CodeGeneratieRestAPI.exceptions.*;
import com.example.CodeGeneratieRestAPI.helpers.ServiceHelper;
import com.example.CodeGeneratieRestAPI.models.*;
import com.example.CodeGeneratieRestAPI.repositories.AccountRepository;
import com.example.CodeGeneratieRestAPI.repositories.TransactionRepository;
import com.example.CodeGeneratieRestAPI.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Transaction> getAll(User user, Date startDate, Date endDate, String search) {

        Date startOfDay = getStartOfDay(startDate);
        Date endOfDay = getEndOfDay(endDate);

        //Check if user is not an employee and if the doesn't user owns the account
        if (!user.getUserType().equals(UserType.EMPLOYEE)) {
            throw new EmployeeOnlyException("This user is not an employee.");
        }

        return transactionRepository.findAll(endOfDay, startOfDay, search);
    }

    public List<Transaction> getAllByUser(User user) {
        return transactionRepository.findAllByUserId(user.getId());
    }

    public Transaction add(User user, TransactionRequestDTO transactionIn) {
        String transactionToAccount = transactionIn.getToAccountIban();
        String transactionFromAccount = transactionIn.getFromAccountIban();
        Account fromAccount = transactionFromAccount != null ? accountRepository.findByIban(transactionIn.getFromAccountIban()) : null;
        Account toAccount = transactionToAccount != null ? accountRepository.findByIban(transactionIn.getToAccountIban()) : null;

        TransactionType transactionType = null;
        try {
            transactionType = TransactionType.valueOf(transactionIn.getTransactionType());
        } catch (IllegalArgumentException e) {
            throw new TransactionTypeNotValidException("The transaction type is not valid.");
        }

        Transaction transaction = new Transaction(fromAccount, toAccount, transactionIn.getAmount(), transactionIn.getLabel(), transactionIn.getDescription(), transactionType);

        validateTransactionAmount(transaction);

        switch (transaction.getTransactionType()) {
            case DEPOSIT -> {
                if (toAccount == null) {
                    throw new TransactionAccountNotValidException("The to account can't be empty.");
                }
                validateUserOwnsAccount(user, toAccount);

                //Update the account balance
                toAccount.setBalance(toAccount.getBalance() + transaction.getAmount());
                accountRepository.save(toAccount);
            }
            case WITHDRAW -> {
                if (fromAccount == null) {
                    throw new TransactionAccountNotValidException("The from account can't be empty.");
                }

                validateUserOwnsAccount(user, fromAccount);
                validateAbsoluteLimit(fromAccount, transaction);
                validateTransactionLimit(fromAccount, transaction);
                validateDailyLimit(fromAccount, transaction);

                //Update the account balance
                fromAccount.setBalance(fromAccount.getBalance() - transaction.getAmount());
                accountRepository.save(fromAccount);
            }
            case TRANSFER -> {
                if (toAccount == null || fromAccount == null) {
                    throw new TransactionAccountNotValidException("The to or from account can't be empty.");
                }

                //Check if the user owns this account or is an admin
                validateUserOwnsAccount(user, fromAccount);

                //Check if to account is not a savings account and if the user of from account also owns the to account
                if (fromAccount.getIsSavings() && !toAccount.getUser().getUsername().equals(fromAccount.getUser().getUsername())) {
                    throw new TransactionTransferSavingsException("It is not possible to transfer from a savings account to an account that is not your account.");
                }

                //Check if to account is not a savings account and if the user of from account also owns the to account
                if (toAccount.getIsSavings() && !fromAccount.getUser().getUsername().equals(toAccount.getUser().getUsername())) {
                    throw new TransactionTransferSavingsException("It is not possible to transfer to a savings account from an account that is not your account.");
                }
                validateAbsoluteLimit(fromAccount, transaction);
                validateTransactionLimit(fromAccount, transaction);
                validateDailyLimit(fromAccount, transaction);

                //Update the account balance
                fromAccount.setBalance(fromAccount.getBalance() - transaction.getAmount());
                toAccount.setBalance(toAccount.getBalance() + transaction.getAmount());
                accountRepository.save(fromAccount);
                accountRepository.save(toAccount);
            }
            default -> throw new TransactionTypeNotValidException("The transaction type is not valid.");
        }

        transaction.setUser(user);

        transaction.setCreatedAt(java.sql.Timestamp.valueOf(LocalDateTime.now()));

        return transactionRepository.save(transaction);
    }

    public Transaction getById(User user, Long id) {
        return transactionIsOwnedByUser(user, id);
    }

    public Transaction transactionIsOwnedByUser(User user, Long id) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new TransactionNotFoundException("This transaction does not exist."));

        if (transaction.getFromAccount() != null && !transaction.getFromAccount().getUser().getId().equals(user.getId())) {
            throw new TransactionNotOwnedException("This user does not own the specified account");
        } else if (transaction.getToAccount() != null && !transaction.getToAccount().getUser().getId().equals(user.getId())) {
            throw new TransactionNotOwnedException("This user does not own the specified account");
        }
        return transaction;
    }

    public List<Transaction> getAllByAccountIban(User user, String iban, Date startDate, Date endDate, String search) {
        Date startOfDay = getStartOfDay(startDate);
        Date endOfDay = getEndOfDay(endDate);

        //Check if user is not an employee and if the user doesn't own the account
        if (!user.getUserType().equals(UserType.EMPLOYEE) && (user.getAccounts() == null || !user.getAccounts().stream().anyMatch(account -> account.getIban().equals(iban)))) {
            throw new AccountNotOwnedException("This user does not own the specified account");
        }

        return transactionRepository.findAllByIban(endOfDay, startOfDay, iban, search);
    }

    private Double getTodaysAccumulatedTransactionAmount(String iban) {
        LocalDate today = LocalDate.now();
        Date startOfDay = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endOfDay = Date.from(today.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
        List<Transaction> transactions = transactionRepository.findAllByCreatedAtBetweenAndFromAccountIban(startOfDay, endOfDay, iban);
        return transactions.stream().mapToDouble(Transaction::getAmount).sum();
    }

    private void validateTransactionAmount(Transaction transaction) {
        //Check if the transaction amount is not negative
        if (transaction.getAmount() < 0) {
            throw new TransactionAmountNotValidException("The transaction amount can't be negative.");
        }

        //Check if the transaction amount is not zero
        if (transaction.getAmount() == 0) {
            throw new TransactionAmountNotValidException("The transaction amount can't be zero.");
        }
    }

    private void validateTransactionLimit(Account account, Transaction transaction) {
        //Check if the transaction amount didn't exceed the transaction limit
        if (account.getTransactionLimit() < transaction.getAmount()) {
            throw new TransactionExceededTransactionLimitException("The transaction limit for this account has been exceeded.");
        }
    }
    private void validateUserOwnsAccount(User user, Account account) {
        //Check if the user owns this account or is an admin
        if (!user.getUserType().equals(UserType.EMPLOYEE) && !account.getUser().getUsername().equals(user.getUsername())) {
            throw new AccountNotOwnedException("This account does not belong to this user.");
        }
    }
    private void validateDailyLimit(Account account, Transaction transaction) {
        //Check if the transaction amount didn't exceed the total limit
        if (account.getDailyLimit() < getTodaysAccumulatedTransactionAmount(account.getIban()) + transaction.getAmount()) {
            throw new TransactionExceededDailyLimitException("This account exceeded the daily limit.");
        }
    }

    private void validateAbsoluteLimit(Account account, Transaction transaction) {
        //Check if transactions amount doesn't exceed the absolute limit of the account
        if (account.getAbsoluteLimit() > account.getBalance() - transaction.getAmount()) {
            throw new TransactionExceededAbsoluteLimitException("This transaction exceeds the absolute limit of this account.");
        }
    }

    private Date getStartOfDay(Date date) {
        return Date.from(date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    private Date getEndOfDay(Date date) {
        return Date.from(date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
    }
}