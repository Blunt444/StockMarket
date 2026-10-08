package stockmarket;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.Scanner;

@Command(name = "stock", description = "A stock market CLI", subcommands = {Price.class, Portfolio.class})
public class App implements Runnable{
    public static void main(String[] args) {
        // int exitCode = new CommandLine(new App()).execute(args);
        // System.exit(exitCode);

        CommandLine cmd = new CommandLine(new App());

        Scanner sc = new Scanner(System.in);

        while(true){
            System.out.print("StockMarket> ");

            String line = sc.nextLine().trim();

            if(line.isEmpty()) continue;

            if(line.equals("quit") || line.equals("exit")) break;

            cmd.execute(line.split("\\s+"));
        }
    }

    @Override
    public void run(){
        System.out.println("Running");
    }
    
}

