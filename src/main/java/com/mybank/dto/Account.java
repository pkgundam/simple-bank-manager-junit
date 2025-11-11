package com.mybank.dto;

import java.util.Objects;

public class Account {
    private final String accountNumber;
    private String holderName;
    private double balance;

    /**
     * Constructs an Account.
     *
     * @param accountNumber  unique, 4-12 alphanumeric characters
     * @param holderName     non-empty account holder name
     * @param initialBalance must be >= 0
     * @throws IllegalArgumentException if validation fails
     */
    public Account(String accountNumber, String holderName, double initialBalance) {
        validateAccountNumber(accountNumber);
        if (holderName == null || holderName.trim().isEmpty()) {
            throw new IllegalArgumentException("Holder name cannot be empty");
        }
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
        this.accountNumber = accountNumber;
        this.holderName = holderName.trim();
        this.balance = initialBalance;
    }

    private static void validateAccountNumber(String number) {
        if (number == null || !number.matches("[A-Za-z0-9]{4,12}")) {
            throw new IllegalArgumentException("Account number must be 4-12 alphanumeric characters");
        }
    }

    /**
     * @return immutable account number
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * @return holder name
     */
    public String getHolderName() {
        return holderName;
    }

    /**
     * Updates the holder name.
     *
     * @param holderName non-empty name
     */
    public void setHolderName(String holderName) {
        if (holderName == null || holderName.trim().isEmpty()) {
            throw new IllegalArgumentException("Holder name cannot be empty");
        }
        this.holderName = holderName.trim();
    }

    /**
     * @return current balance
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Deposits a positive, non-zero amount.
     *
     * @param amount amount to deposit (> 0)
     */
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be > 0");
        }
        balance += amount;
    }

    /**
     * Withdraws a positive, non-zero amount if sufficient funds exist.
     *
     * @param amount amount to withdraw (>0)
     */
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be > 0");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        balance -= amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        Account account = (Account) o;
        return accountNumber.equalsIgnoreCase(account.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber.toLowerCase());
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%.2f", accountNumber, holderName, balance);
    }
}