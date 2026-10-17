package stockmarket;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.Scanner;

@Command(name = "stockmarket", description = "A stock market CLI", subcommands = { Price.class, Portfolio.class,
        Stock.class })
public class App implements Runnable {
    static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        CommandLine cmd = new CommandLine(new App());

        cmd.setParameterExceptionHandler((ex, a) -> {
            System.out.println("Error : " + ex.getMessage());
            return 2;
        });

        System.out.println("Get a free key at https://finnhub.io/register (it's on the dashboard).");
        System.out.print("Enter the api key: ");

        String key = sc.nextLine().trim().split("\\s+")[0];
        DataService.init(key);

        while (true) {
            DataService.authenticateKey();
            DataService.callApiKey();
            System.out.print("\nStockMarket> ");

            String line = sc.nextLine().trim().toLowerCase();

            if (line.isEmpty())
                continue;

            if (line.equals("quit") || line.equals("exit"))
                break;

            cmd.execute(line.split("\\s+"));
        }
    }

    @Override
    public void run() {
        System.out.println("Running");
    }

}
