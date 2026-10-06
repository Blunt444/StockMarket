    package stockmarket;

    import java.util.Map;

    public class DataService{
        private static final Map<String, Data> data = Map.of(
            "aapl", new Data("aapl",120),
            "tesl", new Data("tesl",130), 
            "micr", new Data("micr",200)
        );

        public Double getPrice(String name){
            Data val = data.get(name.toLowerCase());

            if(val == null) return null; 

            return val.price;
        }
    }