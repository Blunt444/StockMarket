package stockmarket;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.List;
import java.util.Map;

@Command(name = "stock", subcommands = { Stock.Info.class, Stock.ListStock.class })
public class Stock {

    private static final DataService service = new DataService();

    @Command(name = "info")
    public static class Info implements Runnable {

        @Parameters(index = "0")
        private String stockName;

        @Override
        public void run() {
            Data data = service.getStock(stockName);

            if (data == null) {
                System.out.println("Stock '" + stockName.toUpperCase() + "' can not be found in service.");
                return;
            }

            System.out.println("Stock Name: " + stockName.toUpperCase() + " ,Price: $" + data.price);
        }
    }

    @Command(name = "list")
    public static class ListStock implements Runnable {
        @Parameters(index = "0", arity = "0..1", defaultValue = "10")
        private int amount;

        @Override
        public void run() {
            int offset = 0;
            List<Map.Entry<String, Data>> all = service.getAllStock();
            boolean again = false;

            do {

                int len = Math.min(offset + amount, all.size());

                for (int i = offset; i < len; i++) {
                    Map.Entry<String, Data> entry = all.get(i);
                    String key = entry.getKey();
                    Data value = entry.getValue();

                    System.out.println("Stock Name: " + key.toUpperCase() + " ,Price: $" + value.price);
                }

                if (len >= all.size()) {
                    System.out.println("All stocks in service have been listed");
                    break;
                }

                System.out.print("Do you want to continue(y/n): ");

                String confirmation = App.sc.nextLine().trim().toLowerCase();

                if (confirmation.equals("yes") || confirmation.equals("y")) {
                    again = true;
                    System.out.print("\nHow many more: ");
                    amount = App.sc.nextInt();
                    System.out.print("\n");

                    if (amount < 0) {
                        System.out.println("Invalid Input. Input must be more than 0.");
                        break;
                    }
                } else {
                    System.out.println("Quiting Listing...");
                    break;
                }

                offset = len;

            } while (again);
        }
    }
}
