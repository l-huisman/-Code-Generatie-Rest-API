package com.example.CodeGeneratieRestAPI.services;

import com.example.CodeGeneratieRestAPI.models.Account;
import com.example.CodeGeneratieRestAPI.models.Transaction;
import com.example.CodeGeneratieRestAPI.models.TransactionType;
import com.example.CodeGeneratieRestAPI.models.User;
import com.example.CodeGeneratieRestAPI.models.UserType;
import com.example.CodeGeneratieRestAPI.repositories.AccountRepository;
import com.example.CodeGeneratieRestAPI.repositories.TransactionRepository;
import com.example.CodeGeneratieRestAPI.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        if (!user.getUserType().contains(UserType.EMPLOYEE) && !user.getAccounts().stream().anyMatch(account -> account.getIban().equals(fromAccountIban))) {
            throw new RuntimeException("This user does not own the specified account");
        }

        return transactionRepository.findAllByCreatedAtLessThanEqualAndCreatedAtGreaterThanEqualAndFromAccountIbanAndDescriptionContainingOrLabelContaining(startDate, endDate, fromAccountIban, description, label);
    }

    public Transaction add(Transaction transaction, String username) {
        Account fromAccount = accountRepository.findByIban(transaction.getFromAccount().getIban());

        if (fromAccount.getBalance() + fromAccount.getAbsoluteLimit() < transaction.getAmount()) {
            throw new RuntimeException("This account does not have enough balance to complete this transaction.");
        }

        //Check negative amount
        if (transaction.getAmount() < 0) {
            throw new RuntimeException("The transaction amount can not be negative.");
        }

        //Check if the user owns this account or is an admin
        User user = userRepository.findUserByUsername(username).get();
        if (!user.getUserType().contains(UserType.EMPLOYEE) && !fromAccount.getUser().getUsername().equals(user.getUsername())) {
            throw new RuntimeException("This account does not belong to this user.");
        }

        //Check if the account is a savings account and if the transaction is a deposit
        if (fromAccount.getIsSavings() && transaction.getTransactionType() != TransactionType.WITHDRAW) {
            throw new RuntimeException("A savings account can not be used for withdraws.");
        }

        //Check if the transaction is a transfer and if there is a toAccountId
        if (transaction.getTransactionType() == TransactionType.TRANSFER && transaction.getToAccount().getIban() == null) {
            throw new RuntimeException("A transfer transaction requires a toAccountId.");
        }

        //Check if the transaction amount didn't exceed the transaction limit
        if (fromAccount.getTransactionLimit() < transaction.getAmount()) {
            throw new RuntimeException("The daily limit for this account has been exceeded.");
        }

        //Check if the transaction amount didn't exceed the total limit
        LocalDate today = LocalDate.now();
        Date startOfDay = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endOfDay = Date.from(today.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
        List<Transaction> transactions = transactionRepository.findAllByCreatedAtBetweenAndFromAccountIban(startOfDay, endOfDay, fromAccount.getIban());
        double accumulatedAmount = transactions.stream()
                                        .mapToDouble(Transaction::getAmount)
                                        .sum();
        if (fromAccount.getDailyLimit() < accumulatedAmount + transaction.getAmount()) {
            throw new RuntimeException("This account exceded the daily limit.");
        }

        transaction.setUser(user);

        //Update the account balance
        fromAccount.setBalance(fromAccount.getBalance() - transaction.getAmount());

        return transactionRepository.save(transaction);
    }

    public Transaction getById(long id, String username) {
        User user = userRepository.findUserByUsername(username).get();

        Transaction transaction = transactionRepository.findById(id).get();

        //Check if user is not an employee and if the user doesn't own the account
        if (!user.getUserType().contains(UserType.EMPLOYEE) && !user.getAccounts().stream().anyMatch(account -> account.getIban().equals(transaction.getFromAccount().getIban()) || account.getIban().equals(transaction.getToAccount().getIban()))) {
            throw new RuntimeException("This user does not own the specified account");
        }

        return transaction;
    }

    public List<Transaction> getAllByAccountIban(String iban, Date startDate, Date endDate, String description, String label, String username) {
        User user = userRepository.findUserByUsername(username).get();

        //Check if user is not an employee and if the user doesn't own the account
        if (!user.getUserType().contains(UserType.EMPLOYEE) && !user.getAccounts().stream().anyMatch(account -> account.getIban().equals(iban))) {
            throw new RuntimeException("This user does not own the specified account");
        }
        
        return transactionRepository.findAllByCreatedAtLessThanEqualAndCreatedAtGreaterThanEqualAndFromAccountIbanOrToAccountIbanAndDescriptionContainingOrLabelContaining(startDate, endDate, iban, iban, description, label);
    }
}