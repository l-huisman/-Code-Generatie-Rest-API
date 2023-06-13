package com.example.CodeGeneratieRestAPI.services;

import com.example.CodeGeneratieRestAPI.dtos.TransactionRequestDTO;
import com.example.CodeGeneratieRestAPI.exceptions.*;
import com.example.CodeGeneratieRestAPI.models.*;
import com.example.CodeGeneratieRestAPI.repositories.AccountRepository;
import com.example.CodeGeneratieRestAPI.repositories.TransactionRepository;
import com.example.CodeGeneratieRestAPI.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public Page<Transaction> getAll(User user, Date startDate, Date endDate, String iban, String amountRelation, Float amount, Integer pageNumber, Integer pageSize) {

        Date startOfDay = getStartOfDay(startDate);
        Date endOfDay = getEndOfDay(endDate);

        if (!amountRelation.isEmpty() && !(amountRelation.equals(">") || amountRelation.equals("<") || amountRelation.equals("="))) {
            throw new RuntimeException("The transaction amount relation is not valid.");
        } else if (!amountRelation.isEmpty() && amount == 0) {
            throw new RuntimeException("The amount filter can not be empty with the amount relation.");
        }

        //Check if user is not an employee and if the doesn't user owns the account
        if (!user.getUserType().equals(UserType.EMPLOYEE)) {
            throw new EmployeeOnlyException("This user is not an employee.");
        }
        Pageable pageableRequest = PageRequest.of(pageNumber, pageSize);

        return transactionRepository.findAll(endOfDay, startOfDay, iban, amountRelation, amount, pageableRequest);
    }

    public List<Transaction> getAllByUser(User user) {
        return transactionRepository.findAllByUserId(user.getId());
    }

    public Transaction add(User user, TransactionRequestDTO transactionIn) {
        String transactionToAccount = transactionIn.getToAccountIban();
        String transactionFromAccount = transactionIn.getFromAccountIban();
        Account fromAccount = transactionFromAccount != null ? accountRepository.findByIban(transactionIn.getFromAccountIban()) : null;
        Account toAccount = transactionToAccount != null ? accountRepository.findByIban(transactionIn.getToAccountIban()) : null;
        TransactionType transactionType = getTransactionType(transactionIn.getTransactionType());
        Transaction transaction = new Transaction(fromAccount, toAccount, transactionIn.getAmount(), transactionIn.getLabel(), transactionIn.getDescription(), transactionType);

        validateTransactionAmount(transaction);

        switch (transaction.getTransactionType()) {
            case DEPOSIT -> {
                validateDeposit(user, toAccount);

                //Update the account balance
                toAccount.setBalance(toAccount.getBalance() + transaction.getAmount());
                accountRepository.save(toAccount);
            }
            case WITHDRAW -> {
                validateWithdraw(user, fromAccount, transaction);

                //Update the account balance
                fromAccount.setBalance(fromAccount.getBalance() - transaction.getAmount());
                accountRepository.save(fromAccount);
            }
            case TRANSFER -> {
                validateTransfer(user, fromAccount, toAccount, transaction);

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

        if (user.getUserType().equals(UserType.EMPLOYEE)) {
            throw new UserOnlyException("This user is not of type USER.");
        }

        if (!user.getUserType().equals(UserType.EMPLOYEE) && transaction.getFromAccount() != null && !transaction.getFromAccount().getUser().getId().equals(user.getId())) {
            throw new TransactionNotOwnedException("This user does not own the specified transaction");
        } else if (!user.getUserType().equals(UserType.EMPLOYEE) && transaction.getToAccount() != null && !transaction.getToAccount().getUser().getId().equals(user.getId())) {
            throw new TransactionNotOwnedException("This user does not own the specified transaction");
        }

        return transaction;
    }

    public Page<Transaction> getAllByAccountIban(User user, String iban, Date startDate, Date endDate, String searchIban, String amountRelation, Float amount, Integer pageNumber, Integer pageSize) {
        Date startOfDay = getStartOfDay(startDate);
        Date endOfDay = getEndOfDay(endDate);

        if (!amountRelation.isEmpty() && !(amountRelation.equals(">") || amountRelation.equals("<") || amountRelation.equals("="))) {
            throw new RuntimeException("The transaction amount relation is not valid.");
        } else if (!amountRelation.isEmpty() && amount == 0) {
            throw new RuntimeException("The amount filter can not be empty with the amount relation.");
        }

        Pageable pageableRequest = PageRequest.of(pageNumber, pageSize);

        //Check if user is not an employee and if the user doesn't own the account
        if (!user.getUserType().equals(UserType.EMPLOYEE) && (user.getAccounts() == null || !user.getAccounts().stream().anyMatch(account -> account.getIban().equals(iban)))) {
            throw new AccountNotOwnedException("This user does not own the specified account");
        }

        return transactionRepository.findAllByIban(endOfDay, startOfDay, iban, searchIban, amountRelation, amount, pageableRequest);
    }

    public Double getTodaysAccumulatedTransactionAmount(String iban) {
        LocalDate today = LocalDate.now();
        Date startOfDay = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endOfDay = Date.from(today.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
        List<Transaction> transactions = transactionRepository.findAllByCreatedAtBetweenAndFromAccountIban(startOfDay, endOfDay, iban);
        return transactions.stream().mapToDouble(Transaction::getAmount).sum();
    }

    private void validateDeposit(User user, Account toAccount) {
        if (toAccount == null) {
            throw new TransactionAccountNotValidException("The to account can't be empty.");
        }

        validateUserOwnsAccount(user, toAccount);
    }

    private void validateWithdraw(User user, Account fromAccount, Transaction transaction) {
        if (fromAccount == null) {
            throw new TransactionAccountNotValidException("The from account can't be empty.");
        }

        validateUserOwnsAccount(user, fromAccount);
        validateAbsoluteLimit(fromAccount, transaction);
        validateTransactionLimit(fromAccount, transaction);
        validateDailyLimit(fromAccount, transaction);
    }

    private void validateTransfer(User user, Account fromAccount, Account toAccount, Transaction transaction) {
        if (toAccount == null || fromAccount == null) {
            throw new TransactionAccountNotValidException("The to or from account can't be empty.");
        }
        validateUserOwnsAccount(user, fromAccount);
        validateSavingsTransfer(fromAccount, toAccount);
        validateAbsoluteLimit(fromAccount, transaction);
        validateTransactionLimit(fromAccount, transaction);
        validateDailyLimit(fromAccount, transaction);
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

    private TransactionType getTransactionType(String type){
        try {
            return TransactionType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new TransactionTypeNotValidException("The transaction type is not valid.");
        }
    }

    private void validateSavingsTransfer(Account fromAccount, Account toAccount) {
        //Check if to account is not a savings account and if the user of from account also owns the to account
        if (fromAccount.getIsSavings() && !toAccount.getUser().getUsername().equals(fromAccount.getUser().getUsername())) {
            throw new TransactionTransferSavingsException("It is not possible to transfer from a savings account to an account that is not your account.");
        }

        //Check if to account is not a savings account and if the user of from account also owns the to account
        if (toAccount.getIsSavings() && !fromAccount.getUser().getUsername().equals(toAccount.getUser().getUsername())) {
            throw new TransactionTransferSavingsException("It is not possible to transfer to a savings account from an account that is not your account.");
        }
    }
}