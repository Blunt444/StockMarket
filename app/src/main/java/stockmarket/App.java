package stockmarket;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "stock", description = "A stock market CLI", subcommands = {Test.class})
public class App implements Runnable{
    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run(){
        System.out.println("Running");
    }
    
}

