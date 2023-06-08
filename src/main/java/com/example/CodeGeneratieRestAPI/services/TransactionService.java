package com.example.CodeGeneratieRestAPI.services;

import com.example.CodeGeneratieRestAPI.dtos.TransactionRequestDTO;
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
import java.util.NoSuchElementException;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Transaction> getAll(Date startDate, Date endDate, String search, String username) {
        User user = userRepository.findUserByUsername(username).get();

        Date startOfDay = Date.from(startDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endOfDay = Date.from(endDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());

        //Check if user is not an employee and if the doesn't user owns the account
        if (!user.getUserType().equals(UserType.EMPLOYEE)) {
            throw new RuntimeException("This user is not an employee.");
        }

        return transactionRepository.findAll(endOfDay, startOfDay, search);
    }

    public Transaction addSeed(Transaction transaction, String username) {
        Account fromAccount = transaction.getFromAccount();
        Account toAccount = transaction.getToAccount();
        User user = userRepository.findUserByUsername(username).get();

        switch (transaction.getTransactionType()) {
            case DEPOSIT:
                //Update the account balance
                toAccount.setBalance(toAccount.getBalance() + transaction.getAmount());
                accountRepository.save(toAccount);
                break;
            case WITHDRAW:
                //Update the account balance
                fromAccount.setBalance(fromAccount.getBalance() - transaction.getAmount());
                accountRepository.save(fromAccount);
                break;
            case TRANSFER:
                //Update the account balance
                fromAccount.setBalance(fromAccount.getBalance() - transaction.getAmount());
                toAccount.setBalance(toAccount.getBalance() + transaction.getAmount());
                accountRepository.save(fromAccount);
                accountRepository.save(toAccount);
                break;
            default:
                throw new RuntimeException("The transaction type is not valid.");
        }

        transaction.setUser(user);

        transaction.setCreatedAt(java.sql.Timestamp.valueOf(LocalDateTime.now()));

        return transactionRepository.save(transaction);
    }

    public Transaction add(TransactionRequestDTO transactionIn, String username) {
        String transactionToAccount = transactionIn.getToAccountIban();
        String transactionFromAccount = transactionIn.getFromAccountIban();
        Account fromAccount = transactionFromAccount != null ? accountRepository.findByIban(transactionIn.getFromAccountIban()) : null;
        Account toAccount = transactionToAccount != null ? accountRepository.findByIban(transactionIn.getToAccountIban()) : null;
        User user = userRepository.findUserByUsername(username).get();

        Transaction transaction = new Transaction(fromAccount, toAccount, transactionIn.getAmount(), transactionIn.getLabel(), transactionIn.getDescription(), transactionIn.getTransactionType());

        //Check negative amount
        if (transaction.getAmount() < 0) {
            throw new RuntimeException("The transaction amount can not be negative.");
        }

        //Check zero amount
        if (transaction.getAmount() == 0) {
            throw new RuntimeException("The transaction amount can not be zero.");
        }

        switch (transaction.getTransactionType()) {
            case DEPOSIT:
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
                break;
            case WITHDRAW:
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
                    throw new RuntimeException("The transaction limit for this account has been exceeded.");
                }

                //Check if the transaction amount didn't exceed the total limit
                if (fromAccount.getDailyLimit() < getTodaysAccumulatedTransactionAmount(fromAccount.getIban()) + transaction.getAmount()) {
                    throw new RuntimeException("This account exceeded the daily limit.");
                }

                //Update the account balance
                fromAccount.setBalance(fromAccount.getBalance() - transaction.getAmount());
                accountRepository.save(fromAccount);
                break;
            case TRANSFER:
                //Check if there is a to and from account
                if (toAccount == null || fromAccount == null) {
                    throw new RuntimeException("The to or from account can't be empty.");
                }

                //Check if the user owns this account or is an admin
                if (!user.getUserType().equals(UserType.EMPLOYEE) && !fromAccount.getUser().getUsername().equals(user.getUsername())) {
                    throw new RuntimeException("This account does not belong to this user.");
                }

                //Check if the transaction amount didn't exceed the transaction limit
                if (fromAccount.getTransactionLimit() < transaction.getAmount()) {
                    throw new RuntimeException("The transaction limit for this account has been exceeded.");
                }

                //Check if the transaction amount didn't exceed the total limit
                if (fromAccount.getDailyLimit() < getTodaysAccumulatedTransactionAmount(fromAccount.getIban()) + transaction.getAmount()) {
                    throw new RuntimeException("This account exceeded the daily limit.");
                }

                //Update the account balance
                fromAccount.setBalance(fromAccount.getBalance() - transaction.getAmount());
                toAccount.setBalance(toAccount.getBalance() + transaction.getAmount());
                accountRepository.save(fromAccount);
                accountRepository.save(toAccount);
                break;
            default:
                throw new RuntimeException("The transaction type is not valid.");
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

        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new RuntimeException("This transaction does not exist."));

        //Check if user is not an employee and if the user doesn't own the account
        if (!user.getUserType().equals(UserType.EMPLOYEE) && !user.getAccounts().stream().anyMatch(account -> account.getIban().equals(transaction.getFromAccount().getIban()) || account.getIban().equals(transaction.getToAccount().getIban()))) {
            throw new RuntimeException("This user does not own the specified account");
        }

        System.out.println(transaction.getLabel());

        return transaction;
    }

    public List<Transaction> getAllByAccountIban(String iban, Date startDate, Date endDate, String search, String username) {
        User user = userRepository.findUserByUsername(username).get();

        //Check if user is not an employee and if the user doesn't own the account
        if (!user.getUserType().equals(UserType.EMPLOYEE) && !user.getAccounts().stream().anyMatch(account -> account.getIban().equals(iban))) {
            throw new RuntimeException("This user does not own the specified account");
        }

        return transactionRepository.findAllByIban(endDate, startDate, iban, search);
    }
}