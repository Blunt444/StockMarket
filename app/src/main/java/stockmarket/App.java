package stockmarket;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.Scanner;

@Command(name = "stockmarket", description = "A stock market CLI", subcommands = { Price.class, Portfolio.class })
public class App implements Runnable {
    static final Scanner sc = new Scanner(System.in);
    public static void main(String[] args) {
        // int exitCode = new CommandLine(new App()).execute(args);
        // System.exit(exitCode);

        CommandLine cmd = new CommandLine(new App());

        cmd.setParameterExceptionHandler((ex, a) -> {
            System.out.println("Error : " + ex.getMessage());
            return 2;
        });


        while (true) {
            System.out.print("StockMarket> ");

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
