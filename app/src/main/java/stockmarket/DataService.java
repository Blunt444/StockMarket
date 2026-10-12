package stockmarket;

import java.util.Map;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;

public class DataService {
    private static final Map<String, Data> data = new TreeMap<>(Map.of(
            "aapl", new Data("aapl", 120),
            "tesl", new Data("tesl", 130),
            "micr", new Data("micr", 200)));

    public Double getPrice(String name) {
        Data val = data.get(name.toLowerCase());

        if (val == null)
            return null;

        return val.price;
    }

    public Data getStock(String stockName) {
        if (data.containsKey(stockName)) {
            return data.get(stockName);
        }
        return null;
    }

    public boolean isStock(String stockName){
        return data.containsKey(stockName);
    }

    public List<Map.Entry<String, Data>> getAllStock() {
        List<Map.Entry<String, Data>> all = new ArrayList<>(data.entrySet());

        return all;
    }
}