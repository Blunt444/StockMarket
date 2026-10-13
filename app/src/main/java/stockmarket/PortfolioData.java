package stockmarket;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class PortfolioData {
    private static Map<String, Holding> holdings = new TreeMap<>();
    private static double balance = 20000;
    private static Set<String> favs = new LinkedHashSet<>();

    private static final DataService service = new DataService();
    private static List<Statement> statements = new ArrayList<>();

    PortfolioData(){
        load();
    }

    public List<Map.Entry<String, Holding>> getHoldings() {

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

            double total = price * quantity;
            Holding holding = holdings.get(name);

            if (total > balance)
                return "Insufficient Balance.";

            if (holding != null) {
                holding.quantity += quantity;
                holding.amount += total;
                holding.pricePerShare = (holding.amount) / (holding.quantity);

            } else {
                holding = new Holding(quantity, total, price);
                holdings.put(name, holding);
            }

            balance -= total;
            addStatement(quantity, total, name, StatementType.BUY, price);
            return "Successfully bought " + quantity + " shares of " + name;
        }

        Holding holding = holdings.get(name);
        double total = price * -quantity;

        if (holding == null)
            return "Seems like " + name + " is not in your holdings";
        else if (holding.quantity < -quantity)
            return "Quantity is greater than what you hold. " + "Actual: " + holding.quantity + " Selling: "
                    + -quantity;

        holding.quantity += quantity;
        holding.amount += holding.pricePerShare * quantity;

        if (holding.quantity == 0)
            holdings.remove(name);

        balance += total;
        addStatement(-quantity, total, name, StatementType.SELL, price);
        return "Successfully sold " + -quantity + " shares of " + name;
    }

    public double getBalance() {
        return balance;
    }

    public void addStatement(int quantity, double transaction, String stockName, StatementType type,
            double pricePerShare) {
        Statement statement = new Statement(quantity, transaction, stockName, type, pricePerShare);
        statements.add(statement);
        save();
    }

    public void addStatement(double transaction, StatementType type) {
        Statement statement = new Statement(transaction, type);
        statements.add(statement);
        save();
    }

    public List<Statement> getStatements(int amount) {
        List<Statement> statement = new ArrayList<>();

        int length = amount >= statements.size() ? 0 : statements.size() - amount;

        for (int i = statements.size() - 1; i >= length; i--) {
            statement.add(statements.get(i));
        }

        return statement;
    }

    public List<Statement> getTrade() {
        List<Statement> result = new ArrayList<>();

        for (int i = statements.size() - 1; i >= 0; i--) {
            Statement statement = statements.get(i);
            if (statement.type == StatementType.BUY || statement.type == StatementType.SELL) {
                result.add(statement);
            }
        }

        return result;
    }

    public boolean deposit(double amount) {
        balance += amount;
        addStatement(amount, StatementType.DEPOSIT);
        return true;
    }

    public boolean withdraw(double amount) {
        if (amount > balance)
            return false;

        balance -= amount;
        addStatement(amount, StatementType.WITHDRAW);

        return true;
    }

    public String addFav(String stockName) {

        if (!service.isStock(stockName)) {
            return "Stock: " + stockName.toUpperCase() + " Couldn't be found in service.";
        } else if (favs.contains(stockName)) {
            return "Stock: " + stockName.toUpperCase() + " is already in favorites";
        }

        favs.add(stockName);
        save();
        return "Stock: " + stockName.toUpperCase() + " have been added to favorites.";
    }

    public String removeFav(String stockName) {

        if (!favs.remove(stockName)) {
            return "Stock: " + stockName.toUpperCase() + " is not in your favorites.";
        }

        save();
        return "Stock: " + stockName.toUpperCase() + " successfully removed from favorites.";
    }

    public Set<String> getFavs(){
        return new LinkedHashSet<>(favs);
    }

    private void load() {
        Storage.SaveData d = Storage.load();
        balance = d.balance;

        holdings = new TreeMap<>(d.holdings);
        statements = new ArrayList<>(d.statements);
        favs.addAll(d.favs);
    }

    private void save() {
        Storage.SaveData d = new Storage.SaveData();

        d.balance = balance;
        d.holdings = new TreeMap<>(holdings);
        d.statements = new ArrayList<>(statements);
        d.favs.addAll(favs);

        Storage.save(d);
    }
}