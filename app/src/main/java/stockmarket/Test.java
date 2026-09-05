package stockmarket;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "Price")
public class Test implements Runnable{
    @Parameters(index = "0")
    private String name;
    @Override
    public void run(){
        System.out.println("Hi" + name);
    }
}