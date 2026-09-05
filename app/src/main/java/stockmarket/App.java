package stockmarket;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "stock", description = "A stock market CLI", subcommands = {Test.class})
public class App {
    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }

    // @Override
    // public void run() {
    //     System.out.println("Welcome to StockMarket CLI! Try running with --help");
    // }
}

// @Command(name = "Price")
// public class Test implements Runnable{
//     @Override
//     public void run(){
//         System.out.println("Hi");
//     }
// }