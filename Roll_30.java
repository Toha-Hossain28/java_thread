// Goal: In this problem, we will learn how to use Threads, Exception Handling and inter-thread communication. 

// Problem Description:

// In this problem, you will be dealing with three types of threads. Please read the following descriptions carefully.

// Thread1 (AccontGenerationThread): This thread will be responsible for creating new accounts for a bank. AccountGenerationThread will create a total of 30 accounts. Each account is associated with three pieces of information - AccountHolderName (a string), AccountNumber (a 12-digit string containing two small English letters at prefix and the remaining 10 digits), and a MaximumTransactionLimit (a random integer between 1-1000 BDT) for the account holder.  AccountGenerationThread should randomly create such accounts. After creating each account, it would sleep for 1000 milliseconds before initiating the creation of another new account. 

// Thread2 (DepositGenerationThread): This thread will be responsible for creating transactions associated with deposits. It will randomly create 500 transactions, wherein for each transaction, you will randomly choose an AccountNumber from the generated accounts in the previous thread and generate DepositAmount (a random value between 1-50000 BDT) to put in a shared array DepositTransactions for processing. After each generation, this thread should sleep for 1000 milliseconds before another generation. 

// Thread3 (WithdrawGenerationThread): This thread will be responsible for creating transactions associated with withdraw. It will randomly create 500 transactions, wherein for each transaction, you will randomly choose an AccountNumber from the generated accounts in the previous thread and generate WithdrawAmount (a random value between 1-100000 BDT) to put in a shared array WithdrawTransactions for processing. After each generation, this thread should sleep for 1000 milliseconds before another generation. 

// Thread4 (DepositProcessingThread1) and Thread5 (DepositProcessingThread2): These two threads will take the transactions from the shared Array DepositTransactions and process them to update the corresponding account’s balance. If the DepositAmount stored in the transaction violates the corresponding account’s MaximumTransactionLimit, they should throw an exception as “Maximum DepositTransaction Limit Violated” and continue processing further transactions. After each processing, DepositProcessingThread1 should wait for 1000 milliseconds and DepositProcessingThread2 should wait for 800 milliseconds. DepositProcessingThread1 and DepositProcessingThread2 can be considered worker threads both serving similar purposes and brought to make the processing faster with different processing limits. 

// Thread6 (WithdrawProcessingThread1) and Thread7 (WithdrawProcessingThread2): These two threads will take the transactions from the shared Array WithdrawTransactions and process them to update the corresponding account’s balance. If the WithdrawAmount stored in the transaction violates the corresponding account’s MaximumTransactionLimit, they should throw an exception as “Maximum WithdrawTransaction Limit Violated” and continue processing further transactions. After each processing, WithdrawProcessingThread1 should wait for 1000 milliseconds and WithdrawProcessingThread2 should wait for 800 milliseconds. WithdrawProcessingThread1 and WithdrawProcessingThread2 can be considered worker threads both serving similar purposes and brought to make the processing faster with different processing limits.

import java.util.*;


class Account {
    String AccountHolderName;
    String AccountNumber;
    int MaximumTransactionLimit;
    int balance;

    public Account(String AccountHolderName, String AccountNumber, int MaximumTransactionLimit) {
        this.AccountHolderName = AccountHolderName;
        this.AccountNumber = AccountNumber;
        this.MaximumTransactionLimit = MaximumTransactionLimit;
        this.balance = 0;
    }

    synchronized public void deposit(int amount) {
        this.balance += amount;
    }

    synchronized public void withdraw(int amount) {
        this.balance -= amount;
    }

    public int getBalance() {
        return this.balance;
    }

    public int getMaximumTransactionLimit() {
        return this.MaximumTransactionLimit;
    }

    public String getAccountNumber() {
        return this.AccountNumber;
    }

    public String getAccountHolderName() {
        return this.AccountHolderName;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void setMaximumTransactionLimit(int MaximumTransactionLimit) {
        this.MaximumTransactionLimit = MaximumTransactionLimit;
    }

    public void setAccountNumber(String AccountNumber) {
        this.AccountNumber = AccountNumber;
    }

    public void setAccountHolderName(String AccountHolderName) {
        this.AccountHolderName = AccountHolderName;
    }

}

class transaction {
    String AccountNumber;
    int amount;

    public transaction(String AccountNumber, int amount) {
        this.AccountNumber = AccountNumber;
        this.amount = amount;
    }

    public String getAccountNumber() {
        return this.AccountNumber;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAccountNumber(String AccountNumber) {
        this.AccountNumber = AccountNumber;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}


class AccountGenerationThread extends Thread {
    public Account[] accounts = new Account[30];
    public int accountCount = 0;


    // Generate accNumber
    public String generateAccountNumber() {
        Random random = new Random();
        String AccountNumber = "";
        for (int i = 0; i < 2; i++) {
            char c = (char) ('a' + random.nextInt(26));;
            AccountNumber = AccountNumber + c;
        }
        for (int i = 0; i < 10; i++) {
            AccountNumber = AccountNumber + random.nextInt(10);
        }
        return AccountNumber;
    }
    // Generate Maximum Transaction Limit
    public int generateMaximumTransactionLimit() {
        Random random = new Random();
        return (1 + random.nextInt(1000));
    }

    // Generate Account Holder Name
    public String generateAccountHolderName() {
        Random random = new Random();
        String name = "";
        for (int i = 0; i < 10; i++) {
            if (i == 0 || i == 5) {
                char c = (char) ('A' + random.nextInt(26));
                name = name + c;
            } else {
                char c = (char) ('a' + random.nextInt(26));
                name = name + c;
            }
            if (i == 5) {
                name = name + " ";
            }
        }
        return name;
    }

    public void run() {
        for (int i = 0; i < 30; i++) {
            String AccountHolderName = generateAccountHolderName();
            String AccountNumber = generateAccountNumber();
            int MaximumTransactionLimit = generateMaximumTransactionLimit();
            Account account = new Account(AccountHolderName, AccountNumber, MaximumTransactionLimit);
            accounts[accountCount++] = account;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class DepositGenerationThread extends Thread {
    public Account[] accounts;
    public transaction[] DepositTransactions = new transaction[500];
    public int depositCount = 0;

    public DepositGenerationThread(Account[] accounts) {
        this.accounts = accounts;
    }

    public void run() {
        Random random = new Random();
        for(int i = 0; i < 500; i++) {
            int accountIndex = random.nextInt(30);
            String AccountNumber = accounts[accountIndex].getAccountNumber();
            int amount = random.nextInt(50000);
            transaction t = new transaction(AccountNumber, amount);
            DepositTransactions[depositCount++] = t;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            
            }
        }
    }
}

class WithdrawGenerationThread extends Thread {
    public Account[] accounts;
    public transaction[] WithdrawTransactions = new transaction[500];
    public int withdrawCount = 0;

    public WithdrawGenerationThread(Account[] accounts) {
        this.accounts = accounts;
    }

    public void run() {
        Random random = new Random();
        for(int i = 0; i < 500; i++) {
            int accountIndex = random.nextInt(30);
            String AccountNumber = accounts[accountIndex].getAccountNumber();
            int amount = random.nextInt(100000);
            transaction t = new transaction(AccountNumber, amount);
            WithdrawTransactions[withdrawCount++] = t;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            
            }
        }
    }
}

class DepositProcessingThread1 extends Thread {
    public Account[] accounts;
    public transaction[] DepositTransactions;

    public DepositProcessingThread1(Account[] accounts, transaction[] DepositTransactions) {
        this.accounts = accounts;
        this.DepositTransactions = DepositTransactions;
    }

    public void run() {
        for (int i = 0; i < 250; i++) {
            String AccountNumber = DepositTransactions[i].getAccountNumber();
            int amount = DepositTransactions[i].getAmount();
            for (int j = 0; j < 30; j++) {
                if (accounts[j].getAccountNumber().equals(AccountNumber)) {
                    if (amount > accounts[j].getMaximumTransactionLimit()) {
                        System.out.println("Maximum DepositTransaction Limit Violated");
                    } else {
                        accounts[j].deposit(amount);
                    }
                    break;
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            
            }
        }
    }
}

class DepositProcessingThread2 extends Thread {
    public Account[] accounts;
    public transaction[] DepositTransactions;

    public DepositProcessingThread2(Account[] accounts, transaction[] DepositTransactions) {
        this.accounts = accounts;
        this.DepositTransactions = DepositTransactions;
    }

    public void run() {
        for (int i = 0; i < 250; i++) {
            String AccountNumber = DepositTransactions[i].getAccountNumber();
            int amount = DepositTransactions[i].getAmount();
            for (int j = 0; j < 30; j++) {
                if (accounts[j].getAccountNumber().equals(AccountNumber)) {
                    if (amount > accounts[j].getMaximumTransactionLimit()) {
                        System.out.println("Maximum DepositTransaction Limit Violated");
                    } else {
                        accounts[j].deposit(amount);
                    }
                    break;
                }
            }
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class WithdrawProcessingThread1 extends Thread {
    public Account[] accounts;
    public transaction[] WithdrawTransactions;

    public WithdrawProcessingThread1(Account[] accounts, transaction[] WithdrawTransactions) {
        this.accounts = accounts;
        this.WithdrawTransactions = WithdrawTransactions;
    }

    public void run() {
        for (int i = 0; i < 250; i++) {
            String AccountNumber = WithdrawTransactions[i].getAccountNumber();
            int amount = WithdrawTransactions[i].getAmount();
            for (int j = 0; j < 30; j++) {
                if (accounts[j].getAccountNumber().equals(AccountNumber)) {
                    if (amount > accounts[j].getMaximumTransactionLimit()) {
                        System.out.println("Maximum WithdrawTransaction Limit Violated");
                    } else {
                        accounts[j].withdraw(amount);
                    }
                    break;
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            
            }
        }
    }
}

class WithdrawProcessingThread2 extends Thread {
    public Account[] accounts;
    public transaction[] WithdrawTransactions;

    public WithdrawProcessingThread2(Account[] accounts, transaction[] WithdrawTransactions) {
        this.accounts = accounts;
        this.WithdrawTransactions = WithdrawTransactions;
    }

    public void run() {
        for (int i = 0; i < 250; i++) {
            String AccountNumber = WithdrawTransactions[i].getAccountNumber();
            int amount = WithdrawTransactions[i].getAmount();
            for (int j = 0; j < 30; j++) {
                if (accounts[j].getAccountNumber().equals(AccountNumber)) {
                    if (amount > accounts[j].getMaximumTransactionLimit()) {
                        System.out.println("Maximum WithdrawTransaction Limit Violated");
                    } else {
                        accounts[j].withdraw(amount);
                    }
                    break;
                }
            }
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

public class Roll_30 {
    public static void main(String[] args) {
        AccountGenerationThread accountGenerationThread = new AccountGenerationThread();
        accountGenerationThread.start();
        try {
            accountGenerationThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Account[] accounts = accountGenerationThread.accounts;
        System.out.println("Account Holder | Name Account Number | Maximum Transaction Limit");
        for( int i = 0; i < 30; i++) {
            System.out.println(accounts[i].getAccountHolderName() + " " + accounts[i].getAccountNumber() + " " + accounts[i].getMaximumTransactionLimit());
        }

        DepositGenerationThread depositGenerationThread = new DepositGenerationThread(accounts);
        WithdrawGenerationThread withdrawGenerationThread = new WithdrawGenerationThread(accounts);

        depositGenerationThread.start();
        withdrawGenerationThread.start();

        try {
            depositGenerationThread.join();
            withdrawGenerationThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Deposit Transactions");
        for (int i = 0; i < 500; i++) {
        System.out.println(depositGenerationThread.DepositTransactions[i].getAccountNumber() + " " + depositGenerationThread.DepositTransactions[i].getAmount());
        }

        System.out.println("Withdraw Transactions");
        for (int i = 0; i < 500; i++) {
        System.out.println(withdrawGenerationThread.WithdrawTransactions[i].getAccountNumber() + " " + withdrawGenerationThread.WithdrawTransactions[i].getAmount());
        }
        
        transaction[] DepositTransactions = depositGenerationThread.DepositTransactions;
        transaction[] WithdrawTransactions = withdrawGenerationThread.WithdrawTransactions;

        transaction[] DepositTransactions1 = new transaction[250];
        transaction[] DepositTransactions2 = new transaction[250];

        for (int i = 0; i < 500; i++) {
            if (i < 250) {
                DepositTransactions1[i] = DepositTransactions[i];
            } else {
                DepositTransactions2[i - 250] = DepositTransactions[i];
            }
        }

        transaction[] WithdrawTransactions1 = new transaction[250];
        transaction[] WithdrawTransactions2 = new transaction[250];

        for(int i = 0; i < 500; i++) {
            if (i < 250) {
                WithdrawTransactions1[i] = WithdrawTransactions[i];
            } else {
                WithdrawTransactions2[i - 250] = WithdrawTransactions[i];
            }
        }


        DepositProcessingThread1 depositProcessingThread1 = new DepositProcessingThread1(accounts,
                DepositTransactions1);
        DepositProcessingThread2 depositProcessingThread2 = new DepositProcessingThread2(accounts,
                DepositTransactions2);

        WithdrawProcessingThread1 withdrawProcessingThread1 = new WithdrawProcessingThread1(accounts,
                WithdrawTransactions1);
        WithdrawProcessingThread2 withdrawProcessingThread2 = new WithdrawProcessingThread2(accounts,
                WithdrawTransactions2);
        
        depositProcessingThread1.start();
        depositProcessingThread2.start();
        withdrawProcessingThread1.start();
        withdrawProcessingThread2.start();
    }
}
