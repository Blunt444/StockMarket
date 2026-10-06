package stockmarket;

import picocli.CommandLine.Parameters;
import picocli.CommandLine.Command;

@Command(name = "Price")
public class Price implements Runnable{

    @Parameters(index = "0")
    private String name; 

    private final DataService service = new DataService();

    @Override
    public void run(){
        Double price = service.getPrice(name);

        if(price == null){
            System.out.println("Unknown Stock Name");
            return;
        }
        
        System.out.printf("%.2f", price);
    } 
}