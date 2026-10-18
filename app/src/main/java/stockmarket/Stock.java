package stockmarket;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;

@Command(name = "stock", subcommands = { Stock.Info.class, Stock.ListStock.class, Stock.Compare.class })
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

            try {
                service.getPrice(stockName);
                System.out.printf("Stock:          %s%n", stockName.toUpperCase());
                System.out.printf("Price:          $%.2f%n", data.price);
                System.out.printf("Change:         %+.2f (%+.2f%%)%n", data.change, data.dp);
                System.out.printf("Open:           $%.2f%n", data.open);
                System.out.printf("High:           $%.2f%n", data.high);
                System.out.printf("Low:            $%.2f%n", data.low);
                System.out.printf("Prev. close:    $%.2f%n", data.pc);
            } catch (IllegalStateException e) {
                System.out.println("Couldn't verify the stock price");
            }
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
                int skipped = 0;

                for (int i = offset; i < len; i++) {
                    Map.Entry<String, Data> entry = all.get(i);
                    String key = entry.getKey();
                    Data data = entry.getValue();
                    try {
                        service.getPrice(key);
                        System.out.printf("Stock:          %s%n", key.toUpperCase());
                        System.out.printf("Price:          $%.2f%n", data.price);
                        System.out.printf("Change:         %+.2f (%+.2f%%)%n", data.change, data.dp);
                        System.out.printf("Open:           $%.2f%n", data.open);
                        System.out.printf("High:           $%.2f%n", data.high);
                        System.out.printf("Low:            $%.2f%n", data.low);
                        System.out.printf("Prev. close:    $%.2f%n", data.pc);
                        System.out.printf("------------------------------%n");
                    } catch (IllegalStateException e) {
                        skipped++;
                    }
                }

                if (skipped > 0) {
                    System.out.println("Skipped: " + skipped + " stocks.");
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
                    try {
                        amount = App.sc.nextInt();
                        App.sc.nextLine();

                        if (amount <= 0) {
                            System.out.println("Invalid Input. Input must be greater than 0.");
                            break;
                        }
                    } catch (InputMismatchException e) {
                        App.sc.nextLine();
                        System.out.println("Not a valid input. Must be greater than 0.");
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

    @Command(name = "compare")
    public static class Compare implements Runnable {

        @Parameters(index = "0")
        public String stock1;
        @Parameters(index = "1")
        public String stock2;

        @Override
        public void run() {
            Data stock1Data = service.getStock(stock1);
            Data stock2Data = service.getStock(stock2);

            if (stock1Data == null) {
                System.out.println("Stock: " + stock1.toUpperCase() + " couldn't be found in service.");
                return;
            } else if (stock2Data == null) {
                System.out.println("Stock: " + stock1.toUpperCase() + " couldn't be found in service.");
                return;
            }
            Double price1 = null;
            Double price2 = null;
            try {
                price1 = service.getPrice(stock1);
            } catch (IllegalStateException e) {
                System.out.println("Couldn't verify price for Stock: " + stock1.toUpperCase());
                return;
            }
            try {
                price2 = service.getPrice(stock2);
            } catch (IllegalStateException e) {
                System.out.println("Couldn't verify price for Stock: " + stock2.toUpperCase());
                return;
            }

            double diff = price1 - price2;
            double percent = (diff / price2) * 100;

            System.out.printf("%-8s $%.2f%n", stock1.toUpperCase(), price1);
            System.out.printf("%-8s $%.2f%n", stock2.toUpperCase(), price2);

            if (diff == 0) {
                System.out.println("Both are priced the same.");
            } else if (diff > 0) {
                System.out.printf("%s is $%.2f higher than %s (%+.2f%%)%n",
                        stock1.toUpperCase(), diff, stock2.toUpperCase(), percent);
            } else {
                System.out.printf("%s is $%.2f higher than %s (%+.2f%%)%n",
                        stock2.toUpperCase(), -diff, stock1.toUpperCase(), -(diff / price1) * 100);
            }

        }
    }
}
