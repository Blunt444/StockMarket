package stockmarket;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "stock", subcommands = { Stock.Info.class })
public class Stock {

    private static final DataService service = new DataService();

    @Command(name = "info")
    public static class Info implements Runnable {

        @Parameters(index = "0")
        private String stockName;

        @Override
        public void run() {
            Data data = service.getDetail(stockName);

            if(data == null){
                System.out.println("Stock '" + stockName.toUpperCase() + "' can not be found in service.");
                return;
            }

            System.out.println("Stock Name: " + stockName.toUpperCase() + " ,Price: $" + data.price);
        }
    }
}
