package stockmarket;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.List;

@Command(name = "portfolio", subcommands = { Portfolio.Buy.class, Portfolio.Sell.class, Portfolio.Balance.class,
        Portfolio.Transaction.class, Portfolio.ListHolding.class, Portfolio.Deposit.class, Portfolio.Withdraw.class })
public class Portfolio implements Runnable {

    private static final DataService service = new DataService();
    private static final PortfolioData portfolioData = new PortfolioData();

    @Override
    public void run() {

    }

    @Command(name = "buy")
    public static class Buy implements Runnable {

        @Parameters(index = "0")
        private String name;
        @Parameters(index = "1")
        private int quantity;

        @Override
        public void run() {

            System.out.print("Do you want to proceed this action(Y/N): ");

            String confirmation = App.sc.nextLine().trim().toLowerCase();

            System.out.print("\n");

            if (confirmation.equals("y")) {
                String message = portfolioData.updateHoldings(name, quantity);
                System.out.println(message);
            } else {
                System.out.println("Cancelled the action.");
            }

            // if (isUpdated) {
            // System.out.println("Successfully bought " + quantity + " shares of " + name);
            // } else {
            // System.out.println("Failed to buy " + quantity + " shares of " + name);
            // }

        }

    }

    @Command(name = "sell")
    public static class Sell implements Runnable {
        @Parameters(index = "0")
        private String name;
        @Parameters(index = "1")
        private int quantity;

        @Override
        public void run() {

            System.out.print("Do you want to proceed this action(Y/N): ");

            String confirmation = App.sc.nextLine().trim().toLowerCase();

            System.out.print("\n");

            if (confirmation.toLowerCase().equals("y")) {
                String message = portfolioData.updateHoldings(name, -quantity);
                System.out.println(message);
            } else {
                System.out.println("Cancelled the action.");
            }
            // if (isUpdated) {
            // System.out.println("Successfully sold " + quantity + " shares of " + name);
            // } else {
            // System.out.println("Failed to sell " + quantity + " shares of " + name);
            // }

        }
    }

    @Command(name = "balance")
    public static class Balance implements Runnable {

        @Override
        public void run() {
            double balance = portfolioData.getBalance();

            System.out.printf("Current Balance : $%.2f\n", balance);
        }
    }

    @Command(name = "statement")
    public static class Transaction implements Runnable {

        @Parameters(index = "0", arity = "0..1", defaultValue = "10")
        private int amount;

        @Override
        public void run() {
            List<Statement> statements = portfolioData.getStatements(amount);

            if (statements.size() == 0) {
                System.out.println("No transaction of any TYPE");
                return;
            }

            for (Statement statement : statements) {
                // System.out.println((statement.transaction > 0 ? "Credited" : "Debited $") +
                // (statement.transaction > 0
                // ? statement.transaction
                // : (statement.transaction * -1))
                // + " for " + statement.quantity + " " + statement.stockName);

                if (statement.type == StatementType.BUY) {
                    System.out.println("Bought " + statement.quantity + " quantity of " + statement.stockName + " for $"
                            + statement.transaction * -1);
                } else if (statement.type == StatementType.SELL) {
                    System.out.println("SOLD " + statement.quantity + " quantity of " + statement.stockName + " for $"
                            + statement.transaction);
                } else if (statement.type == StatementType.WITHDRAW) {
                    System.out.println("Withdrew $" + statement.transaction);
                } else if (statement.type == StatementType.DEPOSIT) {
                    System.out.println("Deposited $" + statement.transaction);
                } else {
                    System.out.println("Error in statement");
                }

            }
        }
    }

    @Command(name = "list")
    public static class ListHolding implements Runnable {
        @Parameters(index = "0", arity = "0..1", defaultValue = "10")
        private int amount;

        @Override
        public void run() {

        }
    }

    @Command(name = "deposit")
    public static class Deposit implements Runnable {

        @Parameters(index = "0")
        private double amount;

        @Override
        public void run() {
            boolean confirmation = portfolioData.deposit(amount);

            if (confirmation) {
                System.out.println("Successfully deposited $" + amount);
            } else {
                System.out.println("Failed to deposit $" + amount);
            }
        }
    }

    @Command(name = "withdraw")
    public static class Withdraw implements Runnable {
        @Parameters(index = "0")
        private double amount;

        @Override
        public void run(){
            boolean confirmation = portfolioData.withdraw(amount);

            if(confirmation){
                System.out.println("Successfully withdrew $" + amount + " Current Balance: " + portfolioData.getBalance());
            }
            else{
                System.out.println("Failed to withdraw. Reason Insufficient Balance. Current Balance: " + portfolioData.getBalance());
            }
        }
    }

}