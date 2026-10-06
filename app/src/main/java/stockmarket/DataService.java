package stockmarket

import java.util.Map;

public class DataService{
    private static final Map<String, Data> data = Map.of(
        "aapl", new Data("aapl",120); 
        "tesl", new Data("tesl",130); 
        "micr", new Data("micr",200); 
    )

    public double getPrice(String name){
        return data.get(name.toLowerCase());
    }
}