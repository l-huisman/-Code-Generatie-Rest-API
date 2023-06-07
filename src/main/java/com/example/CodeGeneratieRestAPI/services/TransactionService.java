package com.example.CodeGeneratieRestAPI.services;

import com.example.CodeGeneratieRestAPI.models.Account;
import com.example.CodeGeneratieRestAPI.models.Transaction;
import com.example.CodeGeneratieRestAPI.models.User;
import com.example.CodeGeneratieRestAPI.models.UserType;
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

    public List<Transaction> getAll(Date startDate, Date endDate, String fromAccountIban, String description, String label, String username) {
        User user = userRepository.findUserByUsername(username).get();

        //Check if user is not an employee and if the doesn't user owns the account
        if (!user.getUserType().equals(UserType.EMPLOYEE) && !user.getAccounts().stream().anyMatch(account -> account.getIban().equals(fromAccountIban))) {
            throw new RuntimeException("This user does not own the specified account");
        }

        return transactionRepository.findAllByCreatedAtLessThanEqualAndCreatedAtGreaterThanEqualAndFromAccountIbanAndDescriptionContainingOrLabelContaining(startDate, endDate, fromAccountIban, description, label);
    }

    public Transaction add(Transaction transaction, String username) {
        Account transactionToAccount = transaction.getToAccount();
        Account fromAccount = accountRepository.findByIban(transaction.getFromAccount().getIban());
        Account toAccount = transactionToAccount != null ? accountRepository.findByIban(transaction.getToAccount().getIban()) : null;
        User user = userRepository.findUserByUsername(username).get();

        //Check negative amount
        if (transaction.getAmount() < 0) {
            throw new RuntimeException("The transaction amount can not be negative.");
        }

        switch (transaction.getTransactionType()) {
            case DEPOSIT -> {
                if (toAccount == null) {
                    throw new RuntimeException("The to account can't be empty.");
                }

                //Check if the user owns this account or is an admin
                if (!user.getUserType().equals(UserType.EMPLOYEE) && !toAccount.getUser().getUsername().equals(user.getUsername())) {
                    throw new RuntimeException("This account does not belong to this user.");
                }

                //Update the account balance
                toAccount.setBalance(toAccount.getBalance() + transaction.getAmount());
                accountRepository.save(toAccount);
            }
            case WITHDRAW -> {
                //Check if the user owns this account or is an admin
                if (!user.getUserType().equals(UserType.EMPLOYEE) && !fromAccount.getUser().getUsername().equals(user.getUsername())) {
                    throw new RuntimeException("This account does not belong to this user.");
                }

                //Check if transactions amount doesn't exceed the absolute limit of the account
                if (fromAccount.getAbsoluteLimit() > fromAccount.getBalance() - transaction.getAmount()) {
                    throw new RuntimeException("This transaction exceeds the absolute limit of this account.");
                }

                //Check if the transaction amount didn't exceed the transaction limit
                if (fromAccount.getTransactionLimit() < transaction.getAmount()) {
                    throw new RuntimeException("The daily limit for this account has been exceeded.");
                }

                System.out.println("kaas");

                //Check if the transaction amount didn't exceed the total limit
                if (fromAccount.getDailyLimit() < getTodaysAccumulatedTransactionAmount(fromAccount.getIban()) + transaction.getAmount()) {
                    throw new RuntimeException("This account exceded the daily limit.");
                }

                //Update the account balance
                fromAccount.setBalance(fromAccount.getBalance() - transaction.getAmount());
                accountRepository.save(fromAccount);
            }
            case TRANSFER -> {
                //Check if there is a to and from account
                if (toAccount == null || fromAccount == null) {
                    throw new RuntimeException("The to or from account can't be empty.");
                }

                //Check if the transaction amount didn't exceed the transaction limit
                if (fromAccount.getTransactionLimit() < transaction.getAmount()) {
                    throw new RuntimeException("The daily limit for this account has been exceeded.");
                }

                //Check if the transaction amount didn't exceed the total limit
                if (fromAccount.getDailyLimit() < getTodaysAccumulatedTransactionAmount(fromAccount.getIban()) + transaction.getAmount()) {
                    throw new RuntimeException("This account exceded the daily limit.");
                }

                //Update the account balance
                fromAccount.setBalance(fromAccount.getBalance() - transaction.getAmount());
                accountRepository.save(fromAccount);
                toAccount.setBalance(toAccount.getBalance() + transaction.getAmount());
                accountRepository.save(toAccount);
            }
            default -> throw new RuntimeException("The transaction type is not valid.");
        }

        transaction.setUser(user);
        transaction.setCreatedAt(java.sql.Timestamp.valueOf(LocalDateTime.now()));

        return transactionRepository.save(transaction);
    }

    private double getTodaysAccumulatedTransactionAmount(String iban) {
        LocalDate today = LocalDate.now();
        Date startOfDay = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endOfDay = Date.from(today.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
        List<Transaction> transactions = transactionRepository.findAllByCreatedAtBetweenAndFromAccountIban(startOfDay, endOfDay, iban);
        return transactions.stream().mapToDouble(Transaction::getAmount).sum();
    }

    public Transaction getById(long id, String username) {
        User user = userRepository.findUserByUsername(username).get();

        Transaction transaction = transactionRepository.findById(id).get();

        //Check if user is not an employee and if the user doesn't own the account
        if (!user.getUserType().equals(UserType.EMPLOYEE) && !user.getAccounts().stream().anyMatch(account -> account.getIban().equals(transaction.getFromAccount().getIban()) || account.getIban().equals(transaction.getToAccount().getIban()))) {
            throw new RuntimeException("This user does not own the specified account");
        }

        return transaction;
    }

    public List<Transaction> getAllByAccountIban(String iban, Date startDate, Date endDate, String description, String label, String username) {
        User user = userRepository.findUserByUsername(username).get();

        //Check if user is not an employee and if the user doesn't own the account
        if (!user.getUserType().equals(UserType.EMPLOYEE) && !user.getAccounts().stream().anyMatch(account -> account.getIban().equals(iban))) {
            throw new RuntimeException("This user does not own the specified account");
        }

        return transactionRepository.findAllByCreatedAtLessThanEqualAndCreatedAtGreaterThanEqualAndFromAccountIbanOrToAccountIbanAndDescriptionContainingOrLabelContaining(startDate, endDate, iban, iban, description, label);
    }
}