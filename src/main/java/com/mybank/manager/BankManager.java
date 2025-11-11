package com.mybank.manager;

import com.mybank.dto.Account;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Provides business logic and stream-based operations over accounts.
 */
public class BankManager {
    private final List<Account> accounts = new ArrayList<>();

    /**
     * Loads accounts from CSV if the file exists; otherwise starts empty.
     *
     * @param csvPath path to CSV file (accountNumber,holderName,balance)
     */
    public BankManager(Path csvPath) {
        if (csvPath != null && Files.exists(csvPath)) {
            try (BufferedReader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty() || line.startsWith("#") || line.startsWith("accountNumber")) continue;
                    String[] parts = line.split(",");
                    if (parts.length < 3) continue; // skip malformed
                    String number = parts[0].trim();
                    String name = parts[1].trim();
                    double bal;
                    try {
                        bal = Double.parseDouble(parts[2].trim());
                    } catch (NumberFormatException e) {
                        continue; // skip invalid numeric rows
                    }
                    try {
                        // ensure uniqueness before adding
                        if (getByNumber(number).isPresent()) continue;
                        accounts.add(new Account(number, name, bal));
                    } catch (IllegalArgumentException ignored) {
                        // skip invalid rows
                    }
                }
            } catch (IOException e) {
                System.err.println("[WARN] Failed to read CSV: " + e.getMessage());
            }
        }
    }

    /**
     * Adds a new account after validating uniqueness of account number.
     *
     * @param accountNumber  unique identifier (4-12 alphanumeric)
     * @param holderName     non-empty name
     * @param initialBalance >= 0
     */
    public Account createAccount(String accountNumber, String holderName, double initialBalance) {
        if (getByNumber(accountNumber).isPresent()) {
            throw new IllegalArgumentException("Account number already exists");
        }
        Account acc = new Account(accountNumber, holderName, initialBalance);
        accounts.add(acc);
        return acc;
    }

    /**
     * Deposits into the account with given number.
     */
    public void deposit(String accountNumber, double amount) {
        Account acc = requireAccount(accountNumber);
        acc.deposit(amount);
    }

    /**
     * Withdraws from the account with given number.
     */
    public void withdraw(String accountNumber, double amount) {
        Account acc = requireAccount(accountNumber);
        acc.withdraw(amount);
    }

    /**
     * @return unmodifiable snapshot of all accounts
     */
    public List<Account> getAllAccounts() {
        return Collections.unmodifiableList(new ArrayList<>(accounts));
    }

    /**
     * Finds accounts whose holder name contains the query (case-insensitive).
     */
    public List<Account> findByName(String query) {
        final String q = query == null ? "" : query.trim().toLowerCase();
        return accounts.stream()
                .filter(a -> a.getHolderName().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    /**
     * Finds accounts with balance in [min, max].
     */
    public List<Account> findByBalanceRange(double min, double max) {
        if (min > max) {
            double t = min;
            min = max;
            max = t; // swap
        }
        double minF = min, maxF = max;
        return accounts.stream()
                .filter(a -> a.getBalance() >= minF && a.getBalance() <= maxF)
                .collect(Collectors.toList());
    }

    /**
     * Total balance using streams.
     */
    public double totalBalance() {
        return accounts.stream().mapToDouble(Account::getBalance).sum();
    }

    /**
     * Average balance using streams; returns 0 for empty list.
     */
    public double averageBalance() {
        return accounts.isEmpty() ? 0.0 : accounts.stream().mapToDouble(Account::getBalance).average().orElse(0.0);
    }

    /**
     * Top N accounts by balance (descending).
     */
    public List<Account> topNByBalance(int n) {
        if (n <= 0) return List.of();
        return accounts.stream()
                .sorted(Comparator.comparingDouble(Account::getBalance).reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

    /**
     * Filters accounts by minimum balance.
     */
    public List<Account> filterByMinBalance(double min) {
        double m = min;
        return accounts.stream().filter(a -> a.getBalance() >= m).collect(Collectors.toList());
    }

    /**
     * Persists accounts to CSV. Creates file if missing.
     */
    public void saveToCsv(Path csvPath) {
        try {
            if (csvPath.getParent() != null) Files.createDirectories(csvPath.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(csvPath, StandardCharsets.UTF_8)) {
                writer.write("accountNumber,holderName,balance\n");
                for (Account a : accounts) {
                    writer.write(String.format("%s,%s,%.2f\n", a.getAccountNumber(), a.getHolderName(), a.getBalance()));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save CSV: " + e.getMessage(), e);
        }
    }

    /**
     * Gets an account by number.
     */
    public Optional<Account> getByNumber(String accountNumber) {
        if (accountNumber == null) return Optional.empty();
        String key = accountNumber.toLowerCase();
        return accounts.stream().filter(a -> a.getAccountNumber().toLowerCase().equals(key)).findFirst();
    }

    private Account requireAccount(String accountNumber) {
        return getByNumber(accountNumber).orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountNumber));
    }
}