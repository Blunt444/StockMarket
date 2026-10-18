package stockmarket;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Command(name = "portfolio", subcommands = { Portfolio.Buy.class, Portfolio.Sell.class, Portfolio.Balance.class,
        Portfolio.Transaction.class, Portfolio.ListHolding.class, Portfolio.Deposit.class, Portfolio.Withdraw.class,
        Portfolio.Trade.class, Portfolio.Favorite.class })
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
            if (amount <= 0) {
                System.out.println("Amount must be more than 0.");
                return;
            }
            List<Statement> statements = portfolioData.getStatements(amount);

            if (statements.size() == 0) {
                System.out.println("No transaction of any TYPE");
                return;
            }

            for (Statement statement : statements) {

                if (statement.type == StatementType.BUY) {
                    System.out.println("Bought " + statement.quantity + " quantity of " + statement.stockName + " for $"
                            + statement.transaction + " Price Per Share You Bought: $" + statement.pricePerShare
                            + " at " + statement.time);
                } else if (statement.type == StatementType.SELL) {
                    System.out.println("SOLD " + statement.quantity + " quantity of " + statement.stockName + " for $"
                            + statement.transaction + " Price Per Share You Sold: $" + statement.pricePerShare + " at "
                            + statement.time);
                } else if (statement.type == StatementType.WITHDRAW) {
                    System.out.println("Withdrew $" + statement.transaction + " at " + statement.time);
                } else if (statement.type == StatementType.DEPOSIT) {
                    System.out.println("Deposited $" + statement.transaction + " at " + statement.time);
                } else {
                    System.out.println("Error in statement");
                }

            }
        }
    }

    @Command(name = "trade")
    public static class Trade implements Runnable {

        @Parameters(index = "0", arity = "0..1", defaultValue = "10")
        private int amount;

        @Override
        public void run() {
            int offset = 0;
            List<Statement> all = portfolioData.getTrade();
            boolean again = false;

            if (amount <= 0) {
                System.out.println("Amount must be greater than 0.");
                return;
            }
            if (all.size() == 0) {
                System.out.println("You have no Trade yet.");
                return;
            }

            do {

                int len = Math.min(offset + amount, all.size());

                for (int i = offset; i < len; i++) {
                    Statement statement = all.get(i);

                    System.out.println("Stock Name: " + statement.stockName.toUpperCase() + " ,Type: " + statement.type
                            + " , Total: $" +
                            statement.transaction + " ,Quantity: " + statement.quantity + " ,Price: $"
                            + statement.pricePerShare + " at " + statement.time);
                }

                offset = len;
                if (offset >= all.size()) {
                    System.out.println("All trades have been listed.");
                    break;
                }

                System.out.print("Do you want to continue(y/n): ");
                String confirmation = App.sc.nextLine().trim().toLowerCase();

                if (confirmation.equals("y") || confirmation.equals("yes")) {
                    again = true;
                    System.out.print("How many more? ");

                    try {
                        amount = App.sc.nextInt();
                        App.sc.nextLine();
                        if (amount <= 0) {
                            System.out.println("Amount must be greater than 0.");
                            break;
                        }
                    } catch (InputMismatchException e) {
                        App.sc.nextLine();
                        System.out.println("Invalid Input. Quiting Listing");
                        break;
                    }

                } else {
                    System.out.println("Quitting Listing...");
                    break;
                }

            } while (again);
        }
    }

    @Command(name = "list")
    public static class ListHolding implements Runnable {
        @Parameters(index = "0", arity = "0..1", defaultValue = "10")
        private int amount;

        @Override
        public void run() {
            List<Map.Entry<String, Holding>> all = portfolioData.getHoldings();
            int offset = 0;
            boolean again = false;

            if (amount <= 0) {
                System.out.println("Amount must be greater than 0.");
                return;
            }
            if (all.size() == 0) {
                System.out.println("You have no holdings yet.");
                return;
            }

            do {

                int len = Math.min(amount + offset, all.size());

                for (int i = offset; i < len; i++) {
                    String stockName = all.get(i).getKey();
                    Holding holding = all.get(i).getValue();

                    try {
                        System.out.println("Stock: " + stockName +
                                " ,Quantity: " + holding.quantity +
                                " ,Total Invested: $" + holding.amount +
                                " ,Avg Buy Price: $" + holding.pricePerShare +
                                " ,Current Pice: $" + service.getPrice(stockName) +
                                " ,Diff: $" + (service.getPrice(stockName) - holding.pricePerShare));
                    } catch (IllegalStateException e) {
                        System.out.println("Couldn't fetch price from API. Stock: " + stockName);
                    }
                }

                offset = len;

                if (offset >= all.size()) {
                    System.out.println("All Holdings are listed...");
                    break;
                }

                System.out.print("Do you want to list more stocks(y/n) ");
                String confirmation = App.sc.nextLine().trim().toLowerCase();
                System.out.println();

                if (confirmation.equals("yes") || confirmation.equals("y")) {
                    again = true;
                    System.out.print("How many more? ");

                    try {
                        amount = App.sc.nextInt();
                        App.sc.nextLine();
                        if (amount <= 0) {
                            System.out.println("Not a valid input. Must be greater than 0.");
                            break;
                        }
                    } catch (InputMismatchException e) {
                        App.sc.nextLine();
                        System.out.println("Invalid Input. Quiting Listing.");
                        break;
                    }
                } else {
                    System.out.println("Quitting the listing...");
                    break;
                }

            } while (again);
        }
    }

    @Command(name = "deposit")
    public static class Deposit implements Runnable {

        @Parameters(index = "0")
        private double amount;

        @Override
        public void run() {

            if (amount <= 0) {
                System.out.println("Amount must be greater than 0.");
                return;
            }

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
        public void run() {

            if (amount <= 0) {
                System.out.println("Amount must be greater than 0.");
                return;
            }

            boolean confirmation = portfolioData.withdraw(amount);

            if (confirmation) {
                System.out.println(
                        "Successfully withdrew $" + amount + " Current Balance: " + portfolioData.getBalance());
            } else {
                System.out.println("Failed to withdraw. Reason: Insufficient Balance. Current Balance: "
                        + portfolioData.getBalance());
            }
        }
    }

    @Command(name = "fav", subcommands = { Favorite.Add.class, Favorite.Remove.class, Favorite.ListFavs.class })
    public static class Favorite {

        @Command(name = "add")
        public static class Add implements Runnable {
            @Parameters(index = "0")
            String stockName;

            @Override
            public void run() {
                String message = portfolioData.addFav(stockName);

                System.out.println(message);
            }
        }

        @Command(name = "remove")
        public static class Remove implements Runnable {
            @Parameters(index = "0")
            String stockName;

            @Override
            public void run() {
                String message = portfolioData.removeFav(stockName);

                System.out.println(message);
            }
        }

        @Command(name = "list")
        public static class ListFavs implements Runnable {
            @Parameters(index = "0", arity = "0..1", defaultValue = "10")
            int amount;

            @Override
            public void run() {
                List<String> favs = new ArrayList<>(portfolioData.getFavs());
                boolean again = false;
                int offset = 0;

                if (amount <= 0) {
                    System.out.println("Amount must be greater than 0.");
                    return;
                } else if (favs.size() == 0) {
                    System.out.println("There are no favorites currently. Add any stock.");
                    return;
                }

                System.out.println("Your Favorite stocks: ");

                do {

                    int len = Math.min(offset + amount, favs.size());

                    for (int i = offset; i < len; i++) {
                        String stockName = favs.get(i);

                        System.out.println("Stock: " + stockName.toUpperCase());
                    }

                    offset = len;

                    if (offset >= favs.size()) {
                        System.out.println("All favorites have been listed");
                        break;
                    }

                    System.out.print("Do you want to continue(y/n): ");

                    String confirmation = App.sc.nextLine().trim().toLowerCase();

                    if (confirmation.equals("y") || confirmation.equals("yes")) {
                        again = true;
                        System.out.print("How many more? ");
                        try {
                            amount = App.sc.nextInt();
                            App.sc.nextLine();

                            if (amount <= 0) {
                                System.out.println("Amount must be greater than 0.");
                                break;
                            }
                        } catch (InputMismatchException e) {
                            App.sc.nextLine();
                            System.out.println("Not a valid input. Must be greater than 0.");
                            break;
                        }
                    } else {
                        System.out.println("Quitting Listing...");
                        break;
                    }

                } while (again);
            }
        }
    }
}