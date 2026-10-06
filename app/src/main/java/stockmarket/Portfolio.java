package stockmarket;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "portfolio", subcommands = {Portfolio.Add.class, Portfolio.Sell.class})
public class Portfolio implements Runnable{

    private static final DataService service = new DataService();
    private static final PortfolioData portfolioData = new PortfolioData();

    @Override
    public void run(){

    }

    @Command(name = "add")
    public static class Add implements Runnable{

        @Parameters(index = "0")
        private String name;
        @Parameters(index = "1")
        private int quantity;

        @Override
        public void run(){
            boolean isUpdated = portfolioData.updateHoldings(name,quantity);

            if(isUpdated){
                System.out.println("Successfully bought " + quantity + "shares of " + name);
            }
            else{
                System.out.println("Failed to buy " + quantity + "shares of" + name);
            }
        }

    }

    @Command(name = "sell")
    public static class Sell implements Runnable{
        @Parameters(index = "0")
        private String name;
        @Parameters(index = "1")
        private int quantity;

        @Override
        public void run(){
            boolean isUpdated = portfolioData.updateHoldings(name,-quantity);

            if(isUpdated){
                System.out.println("Successfully sold " + quantity + "shares of " + name);
            }
            else{
                System.out.println("Failed to sell " + quantity + "shares of" + name);
            }
        }
    }

}