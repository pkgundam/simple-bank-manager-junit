package com.mybank.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void constructor_validatesInput() {
        assertThrows(IllegalArgumentException.class, () -> new Account(null, "Alice", 0));
        assertThrows(IllegalArgumentException.class, () -> new Account("A!", "Alice", 0));
        assertThrows(IllegalArgumentException.class, () -> new Account("A123", "", 0));
        assertThrows(IllegalArgumentException.class, () -> new Account("A123", "Alice", -1));
    }

    @Test
    void deposit_increasesBalance() {
        Account acc = new Account("B123", "Bob", 100);
        acc.deposit(50);
        assertEquals(150, acc.getBalance(), 1e-9);
    }

    @Test
    void deposit_rejectsZeroOrNegative() {
        Account acc = new Account("C123", "Cat", 100);
        assertThrows(IllegalArgumentException.class, () -> acc.deposit(0));
        assertThrows(IllegalArgumentException.class, () -> acc.deposit(-10));
    }

    @Test
    void withdraw_decreasesBalance() {
        Account acc = new Account("D123", "Dan", 100);
        acc.withdraw(30);
        assertEquals(70, acc.getBalance(), 1e-9);
    }

    @Test
    void withdraw_rejectsZeroNegativeOrInsufficient() {
        Account acc = new Account("E123", "Eve", 50);
        assertThrows(IllegalArgumentException.class, () -> acc.withdraw(0));
        assertThrows(IllegalArgumentException.class, () -> acc.withdraw(-10));
        assertThrows(IllegalArgumentException.class, () -> acc.withdraw(100));
    }

    @Test
    void equalsAndHashCode_ignoreCase() {
        Account a1 = new Account("X123", "X", 1);
        Account a2 = new Account("x123", "Y", 2);
        assertEquals(a1, a2);
        assertEquals(a1.hashCode(), a2.hashCode());
    }

    @Test
    void toString_formatIncludesAllFields() {
        Account a = new Account("Z999", "Zara", 123.45);
        String s = a.toString();
        assertTrue(s.contains("Z999"));
        assertTrue(s.contains("Zara"));
        assertTrue(s.contains("123.45"));
    }

    @Test
    void setHolderName_validatesInput() {
        Account a = new Account("N123", "Nina", 10);
        a.setHolderName("NewName");
        assertEquals("NewName", a.getHolderName());
        assertThrows(IllegalArgumentException.class, () -> a.setHolderName(""));
    }
}