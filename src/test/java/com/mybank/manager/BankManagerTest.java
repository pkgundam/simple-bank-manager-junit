package com.mybank.manager;

import com.mybank.dto.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BankManagerTest {
    private BankManager manager;
    private Path tempCsv;

    @BeforeEach
    void setup() throws Exception {
        tempCsv = Files.createTempFile("accounts", ".csv");
        // start with empty file
        Files.writeString(tempCsv, "accountNumber,holderName,balance\n");
        manager = new BankManager(tempCsv);
    }

    @Test
    void createAccount_and_uniqueness() {
        Account a = manager.createAccount("A123", "Alice", 100);
        assertEquals("A123", a.getAccountNumber());
        assertThrows(IllegalArgumentException.class, () -> manager.createAccount("A123", "Another", 0));
    }

    @Test
    void deposit_positive() {
        manager.createAccount("B222", "Bob", 50);
        manager.deposit("B222", 25);
        assertEquals(75, manager.getByNumber("B222").get().getBalance(), 1e-9);
    }

    @Test
    void deposit_zero_or_negative_rejected() {
        manager.createAccount("C333", "Cat", 10);
        assertThrows(IllegalArgumentException.class, () -> manager.deposit("C333", 0));
        assertThrows(IllegalArgumentException.class, () -> manager.deposit("C333", -5));
    }

    @Test
    void withdraw_sufficient() {
        manager.createAccount("D444", "Dan", 100);
        manager.withdraw("D444", 40);
        assertEquals(60, manager.getByNumber("D444").get().getBalance(), 1e-9);
    }

    @Test
    void withdraw_insufficient_rejected() {
        manager.createAccount("E555", "Eve", 20);
        assertThrows(IllegalArgumentException.class, () -> manager.withdraw("E555", 25));
    }

    @Test
    void findByName_contains_caseInsensitive() {
        manager.createAccount("F111", "Alice Johnson", 10);
        manager.createAccount("F112", "ALICIA Keys", 20);
        List<Account> r = manager.findByName("ali");
        assertEquals(2, r.size());
    }

    @Test
    void findByBalanceRange_inclusive() {
        manager.createAccount("G001", "G1", 5);
        manager.createAccount("G002", "G2", 10);
        manager.createAccount("G003", "G2", 15);
        List<Account> r = manager.findByBalanceRange(10, 15);
        assertEquals(2, r.size());
    }

    @Test
    void total_and_average_balance() {
        manager.createAccount("H001", "H1", 10);
        manager.createAccount("H002", "H2", 20);
        manager.createAccount("H003", "H3", 30);
        assertEquals(60, manager.totalBalance(), 1e-9);
        assertEquals(20, manager.averageBalance(), 1e-9);
    }

    @Test
    void average_balance_empty_is_zero() {
        assertEquals(0.0, manager.averageBalance(), 1e-9);
    }

    @Test
    void top3_by_balance_sorted_desc() {
        manager.createAccount("I001", "I1", 5);
        manager.createAccount("I002", "I2", 50);
        manager.createAccount("I003", "I3", 20);
        manager.createAccount("I004", "I4", 40);
        List<Account> top = manager.topNByBalance(3);
        assertEquals(List.of("I002", "I004", "I003"), top.stream().map(Account::getAccountNumber).toList());
    }

    @Test
    void filterByMinBalance_returns_expected() {
        manager.createAccount("J001", "J1", 99);
        manager.createAccount("J002", "J2", 100);
        manager.createAccount("J003", "J3", 101);
        List<Account> r = manager.filterByMinBalance(100);
        assertEquals(2, r.size());
        assertTrue(r.stream().allMatch(a -> a.getBalance() >= 100));
    }

    @Test
    void save_and_reload_csv_roundTrip() {
        manager.createAccount("K001", "Kay", 7.5);
        manager.createAccount("K002", "Ken", 12.25);
        manager.saveToCsv(tempCsv);
        BankManager reloaded = new BankManager(tempCsv);
        assertEquals(2, reloaded.getAllAccounts().size());
        assertTrue(reloaded.getByNumber("K001").isPresent());
        assertEquals(19.75, reloaded.totalBalance(), 1e-9);
    }
}
