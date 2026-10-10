package stockmarket;

import java.util.Map;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;

public class PortfolioData {
    private static Map<String, Holding> holdings = new TreeMap<>();
    private static double balance = 20000;

    private static final DataService service = new DataService();
    private static List<Statement> statements = new ArrayList<>();

    public List<Map.Entry<String, Holding>> getHoldings(int amount) {

        List<Map.Entry<String, Holding>> all = new ArrayList<>(holdings.entrySet());

        return all;
    }

    public String updateHoldings(String name, int quantity) {
        if (quantity == 0)
            return "0 Quantity really bruh...";

        Double price = service.getPrice(name);
        if (price == null)
            return "Oops service couldn't find the price for the stock: " + name;

        if (quantity > 0) {

            if (price * quantity > balance)
                return "Insufficient Balance.";

            Holding holding;
            if (holdings.containsKey(name)) {
                holding = holdings.get(name);
                holding.quantity += quantity;
            } else {
                holding = new Holding(quantity, price);
                holdings.put(name, holding);
            }

            balance -= price * quantity;
            addStatement(quantity, price * quantity, name, StatementType.BUY);
            return "Successfully bought " + quantity + " shares of " + name;
        }

        if (!holdings.containsKey(name))
            return "Seems like " + name + " is not in your holdings";
        else if (holdings.get(name).quantity < -quantity)
            return "Quantity is greater than what you hold. " + "Actual: " + holdings.get(name) + " Selling: "
                    + -quantity;

        Holding holding = holdings.get(name);
        holding.quantity += quantity;

        if (holding.quantity == 0)
            holdings.remove(name);

        balance += price * -quantity;
        addStatement(-quantity, price * -quantity, name, StatementType.SELL);
        return "Successfully sold " + -quantity + " shares of " + name;
    }

    public double getBalance() {
        return balance;
    }

    public void addStatement(int quantity, double transaction, String stockName, StatementType type) {
        Statement statement = new Statement(quantity, transaction, stockName, type);
        statements.add(statement);
    }

    public void addStatement(double transaction, StatementType type) {
        Statement statement = new Statement(transaction, type);
        statements.add(statement);
    }

    public List<Statement> getStatements(int amount) {
        List<Statement> statement = new ArrayList<>();

        int length = amount >= statements.size() ? 0 : statements.size() - amount;

        for (int i = statements.size() - 1; i >= length; i--) {
            statement.add(statements.get(i));
        }

        return statement;
    }

    public boolean deposit(double amount) {
        balance += amount;

        return true;
    }

    public boolean withdraw(double amount) {
        if (amount > balance)
            return false;

        balance -= amount;

        return true;
    }
}