package stockmarket;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class PortfolioData {
    private static Map<String, Integer> holdings = new HashMap<>();
    private static double balance = 20000;

    private static final DataService service = new DataService();
    private static List<Statement> statements = new ArrayList<>();

    public boolean updateHoldings(String name, int quantity) {
        if (quantity == 0)
            return true;

        Double price = service.getPrice(name);
        if (price == null)
            return false;

        if (quantity > 0) {

            if (price * quantity > balance)
                return false;

            holdings.put(name, holdings.getOrDefault(name, 0) + quantity);
            balance -= price * quantity;
            addStatement(quantity, -price * quantity, name);
            return true;
        }

        if (!holdings.containsKey(name) || holdings.get(name) < -quantity)
            return false;

        holdings.put(name, holdings.get(name) - (-quantity));
        balance += price * quantity;
        addStatement(quantity, price * quantity, name);
        return true;
    }

    public double getBalance() {
        return balance;
    }

    public void addStatement(int quantity, double transaction, String stockName) {
        Statement statement = new Statement(quantity, transaction, stockName);
        statements.add(statement);
    }

    public List<Statement> getStatements(int amount){
        List<Statement> statement = new ArrayList<>();

        int length = amount >= statements.size() ? 0 : statements.size() - amount;  

        for(int i = statements.size() - 1; i >= length ; i--){
            statement.add(statements.get(i));
        }

        return  statement;
    }
}