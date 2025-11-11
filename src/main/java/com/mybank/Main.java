package com.mybank;

import com.mybank.dto.Account;
import com.mybank.manager.BankManager;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Path CSV_FILE = Path.of("accounts.csv");

    public static void main(String[] args) {
        BankManager manager = new BankManager(CSV_FILE);
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to Simple Banking System (Streams + JUnit5)");
        while (true) {
            printMenu();
            String choice = sc.nextLine().trim();
            try {
                switch (choice) {
                    case "1":
                        System.out.print("Account number (4-12 alphanum): ");
                        String num = sc.nextLine().trim();
                        System.out.print("Holder name: ");
                        String name = sc.nextLine().trim();
                        System.out.print("Initial balance (>=0): ");
                        double ib = Double.parseDouble(sc.nextLine().trim());
                        manager.createAccount(num, name, ib);
                        System.out.println("Account created.\n");
                        break;
                    case "2":
                        System.out.print("Account number: ");
                        String depAccNum = sc.nextLine().trim();
                        System.out.print("Deposit amount: ");
                        double depAmount = Double.parseDouble(sc.nextLine().trim());
                        manager.deposit(depAccNum, depAmount);
                        System.out.println("Deposited.\n");
                        break;
                    case "3":
                        System.out.print("Account number: ");
                        String withAccNum = sc.nextLine().trim();
                        System.out.print("Withdrawal amount: ");
                        double withdrawalAmount = Double.parseDouble(sc.nextLine().trim());
                        manager.withdraw(withAccNum, withdrawalAmount);
                        System.out.println("Withdrawn.\n");
                        break;
                    case "4":
                        List<Account> all = manager.getAllAccounts();
                        if (all.isEmpty()) System.out.println("No accounts found.");
                        else
                            all.forEach(a -> System.out.printf("%s | %s | %.2f\n", a.getAccountNumber(), a.getHolderName(), a.getBalance()));
                        System.out.println();
                        break;
                    case "5":
                        System.out.print("Search name contains: ");
                        String q = sc.nextLine();
                        manager.findByName(q).forEach(a -> System.out.printf("%s | %s | %.2f\n", a.getAccountNumber(), a.getHolderName(), a.getBalance()));
                        System.out.println();
                        break;
                    case "6":
                        System.out.print("Min balance: ");
                        double min = Double.parseDouble(sc.nextLine().trim());
                        System.out.print("Max balance: ");
                        double max = Double.parseDouble(sc.nextLine().trim());
                        manager.findByBalanceRange(min, max).forEach(a -> System.out.printf("%s | %s | %.2f\n", a.getAccountNumber(), a.getHolderName(), a.getBalance()));
                        System.out.println();
                        break;
                    case "7":
                        System.out.printf("Total: %.2f, Average: %.2f\n\n", manager.totalBalance(), manager.averageBalance());
                        break;
                    case "8":
                        manager.topNByBalance(3).forEach(a -> System.out.printf("%s | %s | %.2f\n", a.getAccountNumber(), a.getHolderName(), a.getBalance()));
                        System.out.println();
                        break;
                    case "9":
                        System.out.print("Minimum balance: ");
                        double mb = Double.parseDouble(sc.nextLine().trim());
                        manager.filterByMinBalance(mb).forEach(a -> System.out.printf("%s | %s | %.2f\n", a.getAccountNumber(), a.getHolderName(), a.getBalance()));
                        System.out.println();
                        break;
                    case "10":
                        manager.saveToCsv(CSV_FILE);
                        System.out.println("Saved to " + CSV_FILE.toAbsolutePath());
                        System.out.println();
                        break;
                    case "0":
                        // Auto-save on exit
                        try {
                            manager.saveToCsv(CSV_FILE);
                        } catch (Exception ignored) {
                        }
                        System.out.println("Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid choice. Try again.\n");
                }
            } catch (Exception e) {
                System.out.println("[ERROR] " + e.getMessage() + "\n");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\nMenu:");
        System.out.println("1. Create account");
        System.out.println("2. Deposit");
        System.out.println("3. Withdraw");
        System.out.println("4. View all accounts");
        System.out.println("5. Search by name (streams)");
        System.out.println("6. Search by balance range (streams)");
        System.out.println("7. Calculate total & average balance (streams)");
        System.out.println("8. Top 3 accounts by balance (streams)");
        System.out.println("9. Filter by minimum balance (streams)");
        System.out.println("10. Save to CSV");
        System.out.println("0. Exit");
        System.out.print("Choose: ");
    }
}
