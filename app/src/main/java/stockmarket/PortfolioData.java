package stockmarket;

import java.util.Map;
import java.util.HashMap;

public class PortfolioData {
    private static Map<String, Integer> holdings = new HashMap<>();
    private static double balance = 0;

    private static final DataService service = new DataService();

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
            return true;
        }

        if (!holdings.containsKey(name) || holdings.get(name) < -quantity)
            return false;

        holdings.put(name, holdings.get(name) - (-quantity));
        balance += price * quantity;
        return true;
    }

    public double getBalance(){
        return balance;
    }
}